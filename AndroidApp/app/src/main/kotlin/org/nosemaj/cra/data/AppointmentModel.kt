package org.nosemaj.cra.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlinx.datetime.Instant

// Yes, we're sharing a model across a few modules.
// Ends up significantly cutting down on maintenance in this small proj.
@Entity
data class AppointmentModel(
    @PrimaryKey
    val id: UUID,
    val patientName: String,
    val startTime: Instant,
    val endTime: Instant,
    val status: AppointmentStatus,
    val lastUpdated: Instant,
) {
    enum class AppointmentStatus {
        Scheduled,
        Recording,
        Paused,
        Completed,
        Cancelled,
    }
}
