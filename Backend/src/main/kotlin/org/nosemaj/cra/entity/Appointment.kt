package org.nosemaj.cra.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Appointment(
    @Id
    val id: UUID,
    val patientName: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
) {
    // JPA requires a no-argument constructor due to Reflection, yuck.
    private constructor() : this(
        id = UUID.randomUUID(),
        patientName = "",
        startTime = "",
        endTime = "",
        status = AppointmentStatus.Scheduled,
    )
}

enum class AppointmentStatus {
    Scheduled,
    Recording,
    Paused,
    Completed,
    Cancelled,
}
