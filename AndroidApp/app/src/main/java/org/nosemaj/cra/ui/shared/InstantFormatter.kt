package org.nosemaj.cra.ui.shared

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toFriendlyString(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return with(localDateTime) {
        "$monthNumber/$dayOfMonth, $hour:$minute"
    }
}