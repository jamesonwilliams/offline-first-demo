package org.nosemaj.cra.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus
import org.nosemaj.cra.data.AppointmentRepository
import org.nosemaj.cra.ui.list.UiEvent.InitialLoad
import org.nosemaj.cra.ui.list.UiEvent.RetryClicked
import org.nosemaj.cra.ui.list.UiState.Content
import org.nosemaj.cra.ui.list.UiState.Error
import org.nosemaj.cra.ui.list.UiState.Loading

@HiltViewModel
class AppointmentListViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is RetryClicked -> refreshUi()
            is InitialLoad -> if (uiState.value is Loading) {
                refreshUi()
            }
        }
    }

    private fun refreshUi(showLoading: Boolean = true) {
        appointmentRepository.monitorAppointments()
            .onStart {
                if (showLoading) {
                    updateState { Loading }
                }
            }
            .onEach { appointments ->
                val appointmentSummaries = appointments.map {
                    AppointmentSummary(
                        id = it.id,
                        patientName = it.patientName,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        status = it.status
                    )
                }
                updateState {
                    if (appointmentSummaries.isNotEmpty()) {
                        Content(appointmentSummaries = appointmentSummaries)
                    } else {
                        Error("No appointments to show!")
                    }
                }
            }
            .catch { throwable ->
                updateState { Error(throwable.localizedMessage) }
            }
            .launchIn(viewModelScope + Dispatchers.IO)
    }

    private suspend fun updateState(updater: (oldState: UiState) -> UiState) {
        withContext(Dispatchers.Main) { _uiState.update(updater) }
    }
}

sealed class UiEvent {
    data object InitialLoad : UiEvent()
    data object RetryClicked : UiEvent()
}

sealed class UiState {
    data object Loading : UiState()
    data class Content(val appointmentSummaries: List<AppointmentSummary>) : UiState()
    data class Error(val message: String?) : UiState()
}

data class AppointmentSummary(
    val id: String,
    val patientName: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus
)
