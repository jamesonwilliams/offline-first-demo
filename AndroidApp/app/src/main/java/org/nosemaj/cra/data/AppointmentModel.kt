package org.nosemaj.cra.data

data class AppointmentModel(
    val id: String,
    val patientName: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
) {
    enum class AppointmentStatus {
        Scheduled,
        Recording,
        Paused,
        Completed,
        Cancelled,
    }
}
