package org.nosemaj.cra.resolver

import org.nosemaj.cra.entity.Appointment
import org.nosemaj.cra.entity.AppointmentStatus
import org.nosemaj.cra.service.AppointmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.OffsetDateTime
import java.util.UUID

@Controller
class AppointmentResolver @Autowired constructor(
    private val appointmentService: AppointmentService,
) {
    @QueryMapping
    fun listAppointments(): List<Appointment> {
        return appointmentService.listAppointments()
    }

    @QueryMapping
    fun getAppointment(
        @Argument id: UUID,
    ): Appointment? {
        return appointmentService.getAppointment(id)
    }

    @MutationMapping
    fun addAppointment(
        @Argument patientName: String,
        @Argument startTime: String,
        @Argument endTime: String,
    ): Appointment {
        return appointmentService.saveAppointment(
            requestedAppointment =
                Appointment(
                    id = UUID.randomUUID(),
                    patientName = patientName,
                    startTime = OffsetDateTime.parse(startTime),
                    endTime = OffsetDateTime.parse(endTime),
                    status = AppointmentStatus.Scheduled,
                    lastUpdated = OffsetDateTime.now(),
                ),
        )
    }

    @MutationMapping
    fun syncAppointments(
        @Argument clientAppointments: List<Appointment>,
    ): List<Appointment> {
        return appointmentService.syncAppointments(clientAppointments)
    }

    @MutationMapping
    fun modifyAppointment(
        @Argument id: UUID,
        @Argument startTime: String? = null,
        @Argument endTime: String? = null,
        @Argument patientName: String? = null,
        @Argument status: AppointmentStatus? = null,
        @Argument lastUpdate: String? = null,
    ): Appointment {
        val existingAppointment =
            appointmentService.getAppointment(id)
                ?: throw Error("No appointment with ID $id to update!")
        val updatedAppointment =
            Appointment(
                id = id,
                startTime = startTime?.let { OffsetDateTime.parse(it) } ?: existingAppointment.startTime,
                endTime = endTime?.let { OffsetDateTime.parse(it) } ?: existingAppointment.endTime,
                patientName = patientName ?: existingAppointment.patientName,
                status = status ?: existingAppointment.status,
                lastUpdated = lastUpdate?.let { OffsetDateTime.parse(lastUpdate) } ?: OffsetDateTime.now(),
            )
        return appointmentService.saveAppointment(updatedAppointment)
    }

    @SubscriptionMapping
    fun appointmentChange(): Flux<Appointment> = appointmentService.monitorAppointments()
}
