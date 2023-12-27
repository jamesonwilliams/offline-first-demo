package org.nosemaj.cra.data

import javax.inject.Inject
import org.nosemaj.cra.data.db.DbAppointmentDataSource
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource

class AppointmentRepository @Inject constructor(
    private val dbAppointmentDataSource: DbAppointmentDataSource,
    private val networkAppointmentDataSource: NetworkAppointmentDataSource
) {
    suspend fun loadAppointments(page: Int): Result<List<AppointmentModel>> {
        return dbAppointmentDataSource.loadPageOfAppointments(page = page)
            .onFailure {
                return networkAppointmentDataSource.listAppointments(page = page)
                    .map { it.results }
                    .onSuccess { appointments ->
                        dbAppointmentDataSource.storeAppointments(*appointments.toTypedArray())
                    }
            }
    }

    suspend fun getAppointment(appointmentId: Int): Result<AppointmentModel> {
        return dbAppointmentDataSource.loadAppointmentById(appointmentId = appointmentId)
            .onFailure {
                return networkAppointmentDataSource.getAppointment(appointmentId)
                    .onSuccess {
                        dbAppointmentDataSource.storeAppointments(it)
                    }
            }
    }
}