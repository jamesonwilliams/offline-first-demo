package org.nosemaj.cra.data.db

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun fromInstant(instant: Instant): String = instant.toString()

    @TypeConverter
    fun toInstant(millis: String): Instant = Instant.parse(millis)
}
