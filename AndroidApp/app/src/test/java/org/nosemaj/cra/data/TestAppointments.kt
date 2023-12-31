package org.nosemaj.cra.data

import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus

object TestAppointments {
    fun randomAppointments(amount: Int): List<AppointmentModel> {
        return names.take(amount).map { name -> randomAppointment(name) }
    }

    fun randomAppointment(name: String? = null) = AppointmentModel(
        id = UUID.randomUUID(),
        patientName = name ?: names.random(),
        startTime = times.random(),
        endTime = times.random().plus(45.minutes),
        status = AppointmentStatus.entries.random(),
        lastUpdated = Clock.System.now()
    )

    private val names = listOf(
        "Beatrice",
        "Morgan",
        "Donovan",
        "Greg",
        "Tobias",
        "Friedrich",
        "Beauford",
        "Bradley"
    )

    private val times = listOf(
        "2023-12-30T15:30:00-06:00",
        "2023-12-30T13:30:00-08:00",
        "2023-12-30T21:30:00-00:00"
    ).map { Instant.parse(it) }
}
