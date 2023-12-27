package org.nosemaj.cra.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus
import org.nosemaj.cra.data.AppointmentRepository
import org.nosemaj.cra.ui.list.UiEvent.InitialLoad
import org.nosemaj.cra.ui.list.UiEvent.RetryClicked
import javax.inject.Inject

@HiltViewModel
class AppointmentListViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is RetryClicked -> refreshUi()
            is InitialLoad -> if (uiState.value is UiState.Loading) {
                refreshUi()
            }
        }
    }

    private fun refreshUi(showLoading: Boolean = true) {
        if (showLoading) {
            _uiState.update { UiState.Loading }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appointmentRepository.loadAppointments()
            }
                .onSuccess { appointments ->
                    val appointmentSummaries = appointments.map {
                        AppointmentSummary(
                            id = it.id,
                            patientName = it.patientName,
                            startTime = it.startTime,
                            endTime = it.endTime,
                            status = it.status,
                        )
                    }
                    _uiState.update {
                        if (appointmentSummaries.isNotEmpty()) {
                            UiState.Content(appointmentSummaries = appointmentSummaries)
                        } else {
                            UiState.Error("No appointments to show!")
                        }
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        UiState.Error(throwable.localizedMessage)
                    }
                }
        }
    }
}

sealed class UiEvent {
    data object InitialLoad : UiEvent()
    data object RetryClicked : UiEvent()
}

sealed class UiState {
    data object Loading: UiState()
    data class Content(val appointmentSummaries: List<AppointmentSummary>): UiState()
    data class Error(val message: String?): UiState()
}

data class AppointmentSummary(
    val id: String,
    val patientName: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
)
