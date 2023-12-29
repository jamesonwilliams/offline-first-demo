package org.nosemaj.cra.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus
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

    suspend fun updateStatus(appointmentId: UUID, appointmentStatus: AppointmentStatus) {
        appointmentDao.getById(appointmentId)
            .copy(
                status = appointmentStatus,
                lastUpdated = Clock.System.now(),
            )
            .let { appointmentDao.upsert(it) }

    }
}
