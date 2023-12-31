package org.nosemaj.cra.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import org.nosemaj.cra.data.AppointmentModel

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointmentmodel ORDER BY startTime ASC")
    fun observeAll(): Flow<List<AppointmentModel>>

    @Query("SELECT * FROM appointmentmodel WHERE id = :appointmentId")
    fun observeById(appointmentId: UUID): Flow<AppointmentModel>

    @Query("SELECT * FROM appointmentmodel WHERE id = :appointmentId")
    suspend fun getById(appointmentId: UUID): AppointmentModel

    @Query("SELECT * FROM appointmentmodel ORDER BY startTime ASC")
    suspend fun getAll(): List<AppointmentModel>

    @Upsert
    suspend fun upsert(vararg appointment: AppointmentModel)

    @Upsert
    suspend fun upsertAll(appointments: List<AppointmentModel>)

    @Delete
    suspend fun delete(appointment: AppointmentModel)
}
