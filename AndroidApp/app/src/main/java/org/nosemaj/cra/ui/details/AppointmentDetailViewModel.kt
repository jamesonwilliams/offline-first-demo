package org.nosemaj.cra.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nosemaj.cra.data.AppointmentRepository
import org.nosemaj.cra.ui.details.UiEvent.InitialLoad
import org.nosemaj.cra.ui.details.UiEvent.RetryClicked
import org.nosemaj.cra.ui.details.UiState.Loading

@HiltViewModel
class AppointmentDetailViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val appointmentId: Int = checkNotNull(savedStateHandle["appointmentId"])
    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is InitialLoad -> loadAppointment()
            is RetryClicked -> loadAppointment()
        }
    }

    private fun loadAppointment() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appointmentRepository.getAppointment(appointmentId = appointmentId)
            }
                .onSuccess { appointment ->
                    _uiState.update {
                        UiState.Content(
                            AppointmentDetail(
                                id = appointment.id,
                                name = appointment.name,
                                imageUrl = appointment.image,
                                status = appointment.status,
                                species = appointment.species,
                                gender = appointment.gender
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { UiState.Error(error.message) }
                }
        }
    }
}

sealed class UiEvent {
    data object InitialLoad : UiEvent()
    data object RetryClicked : UiEvent()
}

sealed class UiState {
    data object Loading : UiState()
    data class Content(val appointmentDetail: AppointmentDetail) : UiState()
    data class Error(val errorMessage: String?) : UiState()
}

data class AppointmentDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String,
    val species: String,
    val gender: String
)
