package org.nosemaj.cra.data

import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import org.nosemaj.cra.data.db.AppointmentDao
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource
import org.nosemaj.cra.util.TimeProvider
import timber.log.Timber

class DefaultAppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val networkAppointmentDataSource: NetworkAppointmentDataSource,
    private val timeProvider: TimeProvider,
) : AppointmentRepository {
    override fun getAppointments(): Flow<List<AppointmentModel>> = appointmentDao.observeAll()

    override fun getAppointment(appointmentId: UUID): Flow<AppointmentModel> {
        return appointmentDao.observeById(appointmentId)
    }

    override suspend fun updateStatus(
        appointmentId: UUID,
        status: AppointmentModel.AppointmentStatus,
    ) {
        appointmentDao
            .getById(appointmentId)
            .copy(
                status = status,
                lastUpdated = timeProvider.now()
            ).let {
                appointmentDao.upsert(it)
                networkAppointmentDataSource
                    .updateAppointment(it)
                    .onFailure { failure ->
                        Timber.w(
                            failure,
                            """
                                Failed to optimistically update $appointmentId.
                                Will update server at next sync.
                            """.trimIndent()
                        )
                    }
            }
    }
}
