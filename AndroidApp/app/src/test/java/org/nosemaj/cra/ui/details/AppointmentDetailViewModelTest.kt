package org.nosemaj.cra.ui.details

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.nosemaj.cra.data.AppointmentModel
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Recording
import org.nosemaj.cra.data.FakeAppointmentRepository
import org.nosemaj.cra.data.TestAppointments
import org.nosemaj.cra.ui.details.UiEvent.InitialLoad
import org.nosemaj.cra.ui.details.UiEvent.RecordRequested
import org.nosemaj.cra.ui.details.UiState.Content
import org.nosemaj.cra.ui.details.UiState.Error
import org.nosemaj.cra.util.TestDispatcherRule

class AppointmentDetailViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var appointment: AppointmentModel
    private lateinit var appointmentRepository: FakeAppointmentRepository
    private lateinit var viewModel: AppointmentDetailViewModel

    @Before
    fun setup() {
        appointment = TestAppointments.randomAppointment()
        appointmentRepository = FakeAppointmentRepository()
        viewModel = AppointmentDetailViewModel(
            appointmentRepository = appointmentRepository,
            savedStateHandle =
            SavedStateHandle(
                initialState = mapOf("appointmentId" to appointment.id.toString())
            ),
            ioDispatcher = testDispatcherRule.testDispatcher,
            mainDispatcher = testDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `presents detail content when model available`() {
        // Arrange
        appointmentRepository.appointments.add(appointment)

        // Act
        viewModel.onEvent(InitialLoad)
        testDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(
            Content(appointment.toAppointmentDetail()),
            viewModel.uiState.value
        )
    }

    @Test
    fun `presents error on network failure`() {
        // Arrange
        appointmentRepository.simulateErrors = true

        // Act
        viewModel.onEvent(InitialLoad)
        testDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(
            Error("Couldn't get appointment!"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `appointment state updated when recording starts`() = runTest {
        // Arrange
        appointmentRepository.appointments.add(appointment)

        // Act
        viewModel.onEvent(InitialLoad)
        testDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(RecordRequested(true))
        testDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(
            appointment.copy(status = Recording),
            appointmentRepository.getAppointment(appointment.id).first()
        )
    }
}
