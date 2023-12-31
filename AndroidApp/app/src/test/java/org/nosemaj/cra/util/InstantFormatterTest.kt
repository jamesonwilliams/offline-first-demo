package org.nosemaj.cra.util

import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class InstantFormatterTest {
    @Test
    fun `date and time converts to friendly display string`() {
        assertEquals(
            "Dec 30, 2023 15:30",
            Instant.parse("2023-12-30T15:30:00-06:00")
                .toFriendlyString()
        )
    }
}
