package org.nosemaj.cra.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.nosemaj.cra.data.AppointmentModel

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointmentmodel WHERE id IN (:appointmentIds) ORDER BY id ASC")
    suspend fun loadAllByIds(appointmentIds: IntArray): List<AppointmentModel>

    @Query("SELECT * FROM appointmentmodel WHERE id = :appointmentId")
    suspend fun loadById(appointmentId: Int): AppointmentModel

    @Insert
    suspend fun insertAll(vararg dbAppointment: AppointmentModel)

    @Delete
    suspend fun delete(dbAppointment: AppointmentModel)
}