package org.nosemaj.cra.data.net

import kotlinx.coroutines.flow.Flow
import org.nosemaj.cra.data.AppointmentModel

interface NetworkAppointmentDataSource {
    suspend fun updateAppointment(appointment: AppointmentModel): Result<AppointmentModel>

    fun observeAppointments(
        currentAppointments: List<AppointmentModel>,
    ): Flow<List<AppointmentModel>>
}
