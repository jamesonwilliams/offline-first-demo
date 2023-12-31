package org.nosemaj.cra.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus

interface AppointmentRepository {
    fun getAppointments(): Flow<List<AppointmentModel>>

    fun getAppointment(appointmentId: UUID): Flow<AppointmentModel>

    suspend fun updateStatus(appointmentId: UUID, status: AppointmentStatus)
}
