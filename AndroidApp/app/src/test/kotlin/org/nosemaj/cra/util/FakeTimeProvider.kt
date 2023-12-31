package org.nosemaj.cra.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeTimeProvider(private val staticTime: Instant = Clock.System.now()) : TimeProvider {
    override fun now() = staticTime
}
