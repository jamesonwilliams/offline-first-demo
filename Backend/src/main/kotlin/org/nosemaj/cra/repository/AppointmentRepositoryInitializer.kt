package org.nosemaj.cra.repository

import org.nosemaj.cra.entity.Appointment
import org.nosemaj.cra.entity.AppointmentStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component
class AppointmentRepositoryInitializer @Autowired private constructor(
    private val appointmentRepository: AppointmentRepository,
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        appointmentRepository.saveAllAndFlush(
            listOf(
                Appointment(
                    id = UUID.fromString("7489aec0-611c-46b4-be63-cb6c4a9c834c"),
                    patientName = "Susie Q.",
                    startTime = OffsetDateTime.parse("2024-03-15T08:30:00-06:00"),
                    endTime = OffsetDateTime.parse("2024-03-15T09:00:00-06:00"),
                    status = AppointmentStatus.Scheduled,
                    lastUpdated = OffsetDateTime.parse("2023-12-28T20:00:00-06:00")
                ),
                Appointment(
                    id = UUID.fromString("055f7e09-279c-47ab-915e-475b248091ec"),
                    patientName = "Charlie M.",
                    startTime = OffsetDateTime.parse("2024-02-22T11:30:00-06:00"),
                    endTime = OffsetDateTime.parse("2024-02-22T12:15:00-06:00"),
                    status = AppointmentStatus.Scheduled,
                    lastUpdated = OffsetDateTime.parse("2023-12-28T20:00:00-06:00")
                )
            )
        )
    }
}
