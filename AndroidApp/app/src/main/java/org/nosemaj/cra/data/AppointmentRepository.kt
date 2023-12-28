package org.nosemaj.cra.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource

class AppointmentRepository @Inject constructor(
    private val networkAppointmentDataSource: NetworkAppointmentDataSource
) {
    fun monitorAppointments(): Flow<List<AppointmentModel>> =
        networkAppointmentDataSource.currentAppointments

    suspend fun getAppointment(appointmentId: String): Result<AppointmentModel> =
        networkAppointmentDataSource.getAppointment(
            appointmentId
        )
}
