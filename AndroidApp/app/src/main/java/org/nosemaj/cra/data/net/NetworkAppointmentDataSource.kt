package org.nosemaj.cra.data.net

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.datetime.Instant
import org.nosemaj.cra.AppointmentChangeSubscription
import org.nosemaj.cra.ModifyAppointmentMutation
import org.nosemaj.cra.SyncAppointmentsMutation
import org.nosemaj.cra.data.AppointmentModel
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Cancelled
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Completed
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Paused
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Recording
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus.Scheduled
import org.nosemaj.cra.fragment.AppointmentFragment
import org.nosemaj.cra.type.AppointmentInput
import org.nosemaj.cra.type.AppointmentStatus
import java.util.UUID
import javax.inject.Inject

class NetworkAppointmentDataSource @Inject constructor(private val apolloClient: ApolloClient) {
    suspend fun updateAppointment(appointment: AppointmentModel) = with(appointment) {
        apolloClient.mutation(
            ModifyAppointmentMutation(
                id = id.toString(),
                lastUpdate = appointment.lastUpdated.toString(),
                startTime = Optional.present(startTime.toString()),
                endTime = Optional.present(endTime.toString()),
                status = Optional.present(status.toAppointmentStatusType()),
                patientName = Optional.present(patientName),
            )
        ).execute()
    }

    fun observeAppointments(currentAppointments: List<AppointmentModel>): Flow<List<AppointmentModel>> {
        return merge(
            syncAppointments(currentAppointments),
            monitorAppointmentChanges().map { listOf(it) }
        )
    }

    private fun monitorAppointmentChanges(): Flow<AppointmentModel> {
        return apolloClient.subscription(
            AppointmentChangeSubscription()
        )
            .toFlow()
            .map {
                it.dataAssertNoErrors.appointmentChange.appointmentFragment.toAppointmentModel()
            }
    }

    private fun syncAppointments(appointments: List<AppointmentModel>): Flow<List<AppointmentModel>> {
        val inputs = appointments.map { it.toAppointmentInput() }
        return apolloClient.mutation(SyncAppointmentsMutation(inputs))
            .toFlow()
            .map {
                it.dataAssertNoErrors
                .syncAppointments
                .map {
                    it.appointmentFragment.toAppointmentModel()
                }
            }
    }

    private fun AppointmentModel.toAppointmentInput(): AppointmentInput {
        return AppointmentInput(
            id = id.toString(),
            patientName = patientName,
            startTime = startTime.toString(),
            endTime = endTime.toString(),
            status = status.toAppointmentStatusType(),
            lastUpdated = lastUpdated.toString(),
        )
    }

    private fun AppointmentFragment.toAppointmentModel(): AppointmentModel = AppointmentModel(
        id = UUID.fromString(id),
        patientName = patientName,
        startTime = Instant.parse(startTime),
        endTime = Instant.parse(endTime),
        status = status.toAppointmentStatusModel(),
        lastUpdated = Instant.parse(lastUpdated),
    )

    private fun AppointmentStatus.toAppointmentStatusModel(): AppointmentModel.AppointmentStatus =
        when (this) {
            AppointmentStatus.Scheduled -> Scheduled
            AppointmentStatus.Paused -> Paused
            AppointmentStatus.Recording -> Recording
            AppointmentStatus.Completed -> Completed
            AppointmentStatus.Cancelled -> Cancelled
            AppointmentStatus.UNKNOWN__ -> throw IllegalStateException(
                "Unknown appointment status: $this"
            )
        }

    private fun AppointmentModel.AppointmentStatus.toAppointmentStatusType(): AppointmentStatus =
        when (this) {
            Scheduled -> AppointmentStatus.Scheduled
            Paused -> AppointmentStatus.Paused
            Recording -> AppointmentStatus.Recording
            Completed -> AppointmentStatus.Completed
            Cancelled -> AppointmentStatus.Cancelled
        }
}
