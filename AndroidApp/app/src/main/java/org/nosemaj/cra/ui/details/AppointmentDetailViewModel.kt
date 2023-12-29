package org.nosemaj.cra.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus
import org.nosemaj.cra.data.AppointmentRepository
import org.nosemaj.cra.ui.details.UiEvent.InitialLoad
import org.nosemaj.cra.ui.details.UiEvent.RetryClicked
import org.nosemaj.cra.ui.details.UiState.Content
import org.nosemaj.cra.ui.details.UiState.Error
import org.nosemaj.cra.ui.details.UiState.Loading
import org.nosemaj.cra.ui.shared.toFriendlyString
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppointmentDetailViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val appointmentId = UUID.fromString(checkNotNull(savedStateHandle["appointmentId"]))
    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is InitialLoad -> loadAppointment()
            is RetryClicked -> loadAppointment()
        }
    }

    private fun loadAppointment() {
        appointmentDetail()
            .onStart {
                updateState { Loading }
            }
            .onEach { detail ->
                updateState { Content(detail) }
            }
            .catch { throwable ->
                updateState { Error(throwable.localizedMessage) }
            }
            .launchIn(viewModelScope + Dispatchers.IO)
    }

    private fun appointmentDetail(): Flow<AppointmentDetail> {
        return appointmentRepository.getAppointment(appointmentId = appointmentId)
            .map { appointment ->
                AppointmentDetail(
                    id = appointment.id,
                    patientName = appointment.patientName,
                    startTime = appointment.startTime.toFriendlyString(),
                    endTime = appointment.endTime.toFriendlyString(),
                    status = appointment.status
                )
            }
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
    data class Content(val appointmentDetail: AppointmentDetail) : UiState()
    data class Error(val message: String?) : UiState()
}

data class AppointmentDetail(
    val id: UUID,
    val patientName: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus
)
