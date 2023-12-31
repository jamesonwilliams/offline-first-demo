package org.nosemaj.cra.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.nosemaj.cra.data.AppointmentModel

@Database(
    entities = [AppointmentModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
}
