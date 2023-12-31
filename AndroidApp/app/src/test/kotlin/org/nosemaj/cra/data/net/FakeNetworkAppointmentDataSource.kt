package org.nosemaj.cra.data.net

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.nosemaj.cra.data.AppointmentModel

class FakeNetworkAppointmentDataSource : NetworkAppointmentDataSource {
    var remoteAppointments = mutableListOf<AppointmentModel>()

    override suspend fun updateAppointment(
        appointment: AppointmentModel,
    ): Result<AppointmentModel> {
        remoteAppointments.removeIf { it.id == appointment.id }
        remoteAppointments.add(appointment)
        return Result.success(appointment)
    }

    override fun observeAppointments(
        currentAppointments: List<AppointmentModel>,
    ): Flow<List<AppointmentModel>> {
        val localIds = currentAppointments.map { it.id }
        val remoteIds = remoteAppointments.map { it.id }
        val syncedValues = buildList {
            addAll(remoteAppointments.filter { !localIds.contains(it.id) })
            addAll(currentAppointments.filter { !remoteIds.contains(it.id) })
            addAll(resolve(currentAppointments, remoteAppointments))
        }
        return flowOf(syncedValues.sortedBy { it.startTime })
    }

    private fun resolve(locals: List<AppointmentModel>, remotes: List<AppointmentModel>): List<AppointmentModel> {
        val remoteIds = remotes.map { it.id }
        val localWithServerDupe = locals.filter { remoteIds.contains(it.id) }
        return localWithServerDupe.mapNotNull { local ->
            remotes.firstOrNull { it.id == local.id }
                ?.let { remote ->
                    if (local.lastUpdated > remote.lastUpdated) {
                        local
                    } else {
                        remote
                    }
                }
        }
    }
}
