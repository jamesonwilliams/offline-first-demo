package org.nosemaj.cra.service

import org.nosemaj.cra.entity.Appointment
import org.nosemaj.cra.repository.AppointmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.UUID

@Service
class AppointmentService @Autowired constructor(
    private val appointmentRepository: AppointmentRepository,
) {
    private val updates: Sinks.Many<Appointment> = Sinks.many().multicast().directBestEffort()

    fun saveAppointment(requestedAppointment: Appointment): Appointment {
        val savedAppointment = appointmentRepository.saveAndFlush(requestedAppointment)
        updates.tryEmitNext(savedAppointment)
        return savedAppointment
    }

    fun listAppointments(): List<Appointment> = appointmentRepository.findAll(Sort.by(Sort.Direction.ASC, "startTime"))

    fun getAppointment(id: UUID): Appointment? = appointmentRepository.findByIdOrNull(id)

    fun monitorAppointments(): Flux<Appointment> = updates.asFlux()
}
