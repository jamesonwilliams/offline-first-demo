package org.nosemaj.cra.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.nosemaj.cra.data.db.AppointmentDao
import java.util.UUID
import javax.inject.Inject

class AppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao,
) {
    fun getAppointments(): Flow<List<AppointmentModel>> {
        return appointmentDao.observeAll()
    }

    fun getAppointment(appointmentId: UUID): Flow<AppointmentModel> {
        return appointmentDao.observeById(appointmentId)
    }

    suspend fun saveAppointment(appointment: AppointmentModel) {
        appointmentDao.upsert(appointment.copy(lastUpdated = Clock.System.now()))
    }
}
