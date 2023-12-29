package org.nosemaj.cra.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.OffsetDateTime
import java.util.*

@Entity
data class Appointment(
    @Id
    val id: UUID,
    val patientName: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val status: AppointmentStatus,
    val lastUpdated: OffsetDateTime,
) {
    // JPA requires a no-argument constructor due to Reflection, yuck.
    private constructor() : this(
        id = UUID.randomUUID(),
        patientName = "",
        startTime = OffsetDateTime.now(),
        endTime = OffsetDateTime.now(),
        status = AppointmentStatus.Scheduled,
        lastUpdated = OffsetDateTime.now(),
    )
}

enum class AppointmentStatus {
    Scheduled,
    Recording,
    Paused,
    Completed,
    Cancelled,
}
