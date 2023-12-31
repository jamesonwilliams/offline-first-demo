package org.nosemaj.cra.data.db

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.nosemaj.cra.data.AppointmentModel

class FakeAppointmentDao : AppointmentDao {
    private val dbItems = mutableListOf<AppointmentModel>()

    override fun observeAll(): Flow<List<AppointmentModel>> {
        return flowOf(dbItems.sortedBy { it.startTime })
    }

    override fun observeById(appointmentId: UUID): Flow<AppointmentModel> {
        return flowOf(dbItems.first { it.id == appointmentId })
    }

    override suspend fun getById(appointmentId: UUID): AppointmentModel {
        return dbItems.first { it.id == appointmentId }
    }

    override suspend fun getAll(): List<AppointmentModel> {
        return dbItems.sortedBy { it.startTime }
    }

    override suspend fun upsert(vararg appointment: AppointmentModel) {
        upsertAll(appointment.toList())
    }

    override suspend fun upsertAll(appointments: List<AppointmentModel>) {
        val modifiedIds = appointments.map { it.id }.toSet()
        dbItems.removeIf { modifiedIds.contains(it.id) }
        dbItems.addAll(appointments)
    }

    override suspend fun delete(appointment: AppointmentModel) {
        dbItems.removeIf { appointment.id == it.id }
    }
}
