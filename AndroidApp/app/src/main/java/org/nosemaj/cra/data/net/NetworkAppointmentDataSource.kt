package org.nosemaj.cra.data.net

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import org.nosemaj.cra.GetAppointmentQuery
import org.nosemaj.cra.ListAppointmentsQuery
import org.nosemaj.cra.data.AppointmentModel
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Cancelled
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Completed
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Paused
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Recording
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Scheduled
import org.nosemaj.cra.fragment.AppointmentFragment
import org.nosemaj.cra.type.AppointmentStatus
import javax.inject.Inject

class NetworkAppointmentDataSource @Inject constructor(private val apolloClient: ApolloClient) {
    suspend fun listAppointments(): Result<List<AppointmentModel>> = fetch(
        "No more appointments found.",
        "Error retrieving appointments."
    ) {
        apolloClient.query(ListAppointmentsQuery()).execute()
    }.map { data ->
        data.listAppointments.map { listAppointment ->
            listAppointment.appointmentFragment.toAppointmentModel()
        }
    }

    suspend fun getAppointment(appointmentId: String): Result<AppointmentModel> = fetch(
        "Appointment $appointmentId not found.",
        "Error retrieving appointment $appointmentId."
    ) {
        apolloClient.query(GetAppointmentQuery(appointmentId)).execute()
    }.map { data ->
        data.getAppointment!!.appointmentFragment.toAppointmentModel()
    }

    private fun AppointmentFragment.toAppointmentModel(): AppointmentModel {
        return AppointmentModel(
            id = id,
            patientName = patientName,
            startTime = startTime,
            endTime = endTime,
            status = status.toAppointmentStatusModel(),
        )
    }

    private fun AppointmentStatus.toAppointmentStatusModel(): AppointmentModel.AppointmentStatus {
        return when (this) {
            AppointmentStatus.Scheduled -> Scheduled
            AppointmentStatus.Paused -> Paused
            AppointmentStatus.Recording -> Recording
            AppointmentStatus.Completed -> Completed
            AppointmentStatus.Cancelled -> Cancelled
            AppointmentStatus.UNKNOWN__ -> throw IllegalStateException("Unknown appointment status: $this")
        }
    }

    private suspend fun <T: Operation.Data> fetch(
        noDataMessage: String,
        unsuccessfulMessage: String,
        loadData: suspend () -> ApolloResponse<T>
    ): Result<T> = try {
        val data = loadData()
        if (data.hasErrors()) {
            Result.failure(Error(unsuccessfulMessage))
        } else if (data.data == null) {
            Result.failure(Error(noDataMessage))
        } else {
            Result.success(loadData().dataAssertNoErrors)
        }
    } catch (thr: Throwable) {
        Result.failure(Throwable("Bad network connectivity.", thr))
    }
}
