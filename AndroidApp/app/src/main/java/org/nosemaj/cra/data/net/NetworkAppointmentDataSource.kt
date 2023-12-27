package org.nosemaj.cra.data.net

import javax.inject.Inject
import org.nosemaj.cra.data.AppointmentModel
import retrofit2.Response

class NetworkAppointmentDataSource @Inject constructor(
    private val service: AppointmentService
) {
    suspend fun listAppointments(page: Int): Result<AppointmentListResponse> {
        return fetch(
            "No more appointments found.",
            "Error retrieving appointments."
        ) {
            service.listAppointments(page)
        }
    }

    suspend fun getAppointment(appointmentId: Int): Result<AppointmentModel> {
        return fetch(
            "No appointment $appointmentId found",
            "Error retrieving appointment $appointmentId"
        ) {
            service.getAppointment(appointmentId)
        }
    }

    private suspend fun <T> fetch(
        noDataMessage: String,
        unsuccessfulMessage: String,
        loadData: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = loadData()
            if (response.isSuccessful) {
                response.body()?.let { data -> Result.success(data) }
                    ?: Result.failure(Throwable(noDataMessage))
            } else {
                Result.failure(Throwable(unsuccessfulMessage))
            }
        } catch (thr: Throwable) {
            Result.failure(Throwable("Bad network connectivity.", thr))
        }
    }
}