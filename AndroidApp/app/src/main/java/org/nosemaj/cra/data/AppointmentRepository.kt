package org.nosemaj.cra.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus
import org.nosemaj.cra.data.db.AppointmentDao
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class AppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val networkAppointmentDataSource: NetworkAppointmentDataSource,
) {
    fun getAppointments(): Flow<List<AppointmentModel>> {
        return appointmentDao.observeAll()
    }

    fun getAppointment(appointmentId: UUID): Flow<AppointmentModel> {
        return appointmentDao.observeById(appointmentId)
    }

    suspend fun updateStatus(appointmentId: UUID, status: AppointmentStatus) {
        appointmentDao.getById(appointmentId)
            .copy(
                status = status,
                lastUpdated = Clock.System.now(),
            )
            .let {
                appointmentDao.upsert(it)
                try {
                    networkAppointmentDataSource.updateAppointment(it)
                } catch (error: Throwable) {
                    Timber.w(
                        error,
                        """
                            Failed to optimistically update $appointmentId.
                            Will update server at next sync.
                        """.trimIndent()
                    )
                }
            }
    }
}
