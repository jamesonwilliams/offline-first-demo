package org.nosemaj.cra.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus

class FakeAppointmentRepository : AppointmentRepository {
    var simulateErrors: Boolean = false
    val appointments = mutableListOf<AppointmentModel>()

    override fun getAppointments(): Flow<List<AppointmentModel>> = if (simulateErrors) {
        flow { error("Couldn't get appointments!") }
    } else {
        flowOf(appointments)
    }

    override fun getAppointment(appointmentId: UUID): Flow<AppointmentModel> = if (simulateErrors) {
        flow { error("Couldn't get appointment!") }
    } else {
        flowOf(appointments.first { it.id == appointmentId })
    }

    override suspend fun updateStatus(appointmentId: UUID, status: AppointmentStatus) {
        if (simulateErrors) {
            throw Error("Failed to update status!")
        }
        val appointment = appointments.first { it.id == appointmentId }
        appointments.removeIf { it.id == appointmentId }
        appointments.add(
            appointment.copy(status = status)
        )
    }
}
