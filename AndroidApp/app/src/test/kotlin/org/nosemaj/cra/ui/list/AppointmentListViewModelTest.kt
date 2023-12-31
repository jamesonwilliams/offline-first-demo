package org.nosemaj.cra.ui.list

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.nosemaj.cra.data.AppointmentModel
import org.nosemaj.cra.data.FakeAppointmentRepository
import org.nosemaj.cra.data.TestAppointments
import org.nosemaj.cra.ui.list.UiEvent.InitialLoad
import org.nosemaj.cra.ui.list.UiState.Content
import org.nosemaj.cra.ui.list.UiState.Error
import org.nosemaj.cra.util.TestDispatcherRule

class AppointmentListViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var appointments: List<AppointmentModel>
    private lateinit var appointmentRepository: FakeAppointmentRepository
    private lateinit var viewModel: AppointmentListViewModel

    @Before
    fun setup() {
        appointments = TestAppointments.randomAppointments(2)
        appointmentRepository = FakeAppointmentRepository()
        viewModel = AppointmentListViewModel(
            appointmentRepository = appointmentRepository,
            ioDispatcher = testDispatcherRule.testDispatcher,
            mainDispatcher = testDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `presents summary list content when models available`() {
        // Arrange
        appointmentRepository.appointments.addAll(appointments)

        // Act
        viewModel.onEvent(InitialLoad)
        testDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(
            Content(appointments.map { it.toAppointmentSummary() }),
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
            Error("Couldn't get appointments!"),
            viewModel.uiState.value
        )
    }
}
