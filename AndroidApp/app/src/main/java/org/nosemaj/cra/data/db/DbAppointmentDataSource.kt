package org.nosemaj.cra.data.db

import android.content.Context
import androidx.room.Room
import javax.inject.Inject
import org.nosemaj.cra.data.AppointmentModel

class DbAppointmentDataSource @Inject constructor(applicationContext: Context) {
    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        AppDatabase::class.simpleName
    ).build()

    suspend fun loadAppointmentById(appointmentId: Int): Result<AppointmentModel> {
        return try {
            Result.success(db.appointmentDao().loadById(appointmentId))
        } catch (thr: Throwable) {
            Result.failure(Throwable("Unable to get appointment $appointmentId from DB.", thr))
        }
    }

    suspend fun loadPageOfAppointments(page: Int): Result<List<AppointmentModel>> {
        return try {
            val ids = ((page - 1) * 20 + 1..page * 20).toSet().toIntArray()
            val appointments = db.appointmentDao().loadAllByIds(ids).sortedBy { it.id }
            if (appointments.isEmpty()) {
                return Result.failure(Throwable("No appointments found for page $page."))
            } else {
                return Result.success(appointments)
            }
        } catch (thr: Throwable) {
            Result.failure(Throwable("Unable to fetch appointments for page $page.", thr))
        }
    }

    suspend fun storeAppointments(
        vararg dbAppointment: AppointmentModel
    ): Result<List<AppointmentModel>> = try {
        db.appointmentDao().insertAll(*dbAppointment)
        Result.success(dbAppointment.toList())
    } catch (thr: Throwable) {
        val ids = dbAppointment.map { it.id }
        Result.failure(Throwable("Unable to insert appointments $ids to database.", thr))
    }
}
