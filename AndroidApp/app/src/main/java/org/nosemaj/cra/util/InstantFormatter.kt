package org.nosemaj.cra.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toFriendlyString(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return with(localDateTime) {
        val mon = month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)
        "%3s %d, %04d %d:%02d".format(mon, dayOfMonth, year, hour, minute)
    }
}
