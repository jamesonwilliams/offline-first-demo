package org.nosemaj.cra.ui.list

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
import org.nosemaj.cra.ui.list.UiEvent.InitialLoad
import org.nosemaj.cra.ui.list.UiEvent.RetryClicked
import org.nosemaj.cra.ui.list.UiState.Content
import org.nosemaj.cra.ui.list.UiState.Error
import org.nosemaj.cra.ui.list.UiState.Loading
import org.nosemaj.cra.ui.shared.toFriendlyString
import java.util.UUID
import javax.inject.Inject

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
        appointmentSummaries()
            .onStart {
                if (showLoading) {
                    updateState { Loading }
                }
            }
            .onEach { summaries ->
                updateState {
                    if (summaries.isNotEmpty()) {
                        Content(summaries)
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

    private fun appointmentSummaries(): Flow<List<AppointmentSummary>> {
        return appointmentRepository.getAppointments()
            .map { models ->
                models.map {
                    AppointmentSummary(
                        id = it.id,
                        patientName = it.patientName,
                        startTime = it.startTime.toFriendlyString(),
                        endTime = it.endTime.toFriendlyString(),
                        status = it.status
                    )
                }
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
    data class Content(val appointmentSummaries: List<AppointmentSummary>) : UiState()
    data class Error(val message: String?) : UiState()
}

data class AppointmentSummary(
    val id: UUID,
    val patientName: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus
)
