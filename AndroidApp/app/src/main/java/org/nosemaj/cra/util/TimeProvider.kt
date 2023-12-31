package org.nosemaj.cra.util

import kotlinx.datetime.Instant

interface TimeProvider {
    fun now(): Instant
}
