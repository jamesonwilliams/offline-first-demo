package org.nosemaj.cra.data.db

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun fromInstant(instant: Instant): String {
        return instant.toString()
    }

    @TypeConverter
    fun toInstant(millis: String): Instant {
        return Instant.parse(millis)
    }
}
