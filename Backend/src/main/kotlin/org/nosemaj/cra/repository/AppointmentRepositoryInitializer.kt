package org.nosemaj.cra.repository

import org.nosemaj.cra.entity.Appointment
import org.nosemaj.cra.entity.AppointmentStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.util.*

@Component
class AppointmentRepositoryInitializer @Autowired private constructor(
    private val appointmentRepository: AppointmentRepository,
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        appointmentRepository.saveAllAndFlush(
            listOf(
                Appointment(
                    id = UUID.randomUUID(),
                    patientName = "Susie Q.",
                    startTime = "100",
                    endTime = "200",
                    status = AppointmentStatus.Scheduled,
                ),
                Appointment(
                    id = UUID.randomUUID(),
                    patientName = "Charlie M.",
                    startTime = "500",
                    endTime = "700",
                    status = AppointmentStatus.Scheduled,
                )
            )
        )
    }
}
