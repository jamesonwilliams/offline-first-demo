package org.nosemaj.cra.data

import org.nosemaj.cra.data.net.NetworkAppointmentDataSource
import javax.inject.Inject

class AppointmentRepository @Inject constructor(
    private val networkAppointmentDataSource: NetworkAppointmentDataSource
) {
    suspend fun loadAppointments(): Result<List<AppointmentModel>> {
        return networkAppointmentDataSource.listAppointments()
    }

    suspend fun getAppointment(appointmentId: String): Result<AppointmentModel> {
        return networkAppointmentDataSource.getAppointment(appointmentId)
    }
}
