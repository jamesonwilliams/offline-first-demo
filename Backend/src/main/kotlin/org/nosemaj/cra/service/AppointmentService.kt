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

    fun listAppointments(): List<Appointment> =
        appointmentRepository.findAll(Sort.by(Sort.Direction.ASC, "startTime"))

    fun getAppointment(id: UUID): Appointment? = appointmentRepository.findByIdOrNull(id)

    fun monitorAppointments(): Flux<Appointment> = updates.asFlux()

    fun syncAppointments(clientAppointments: List<Appointment>): List<Appointment> {
        val resolvedAppointments = mutableListOf<Appointment>()
        val serverAppointments = listAppointments()

        val clientIds = clientAppointments.map { it.id }
        val serverIds = serverAppointments.map { it.id }

        // Appointments that exist on server but not client
        // Send them to client
        val serverOnly = serverAppointments.filter { server -> !clientIds.contains(server.id) }
        resolvedAppointments.addAll(serverOnly)

        // Appointments that exist on client but not server
        // Put them in list to return. We'll save them at the end.
        val clientOnly = clientAppointments.filter { client -> !serverIds.contains(client.id) }
        resolvedAppointments.addAll(clientOnly)

        // Appointments which exist on both client and server are more complicated
        // We need to use a conflict resolution strategy.
        clientAppointments
            .mapNotNull { client ->
                serverAppointments.firstOrNull { it.id == client.id }
                    ?.let { serverMatch -> Pair(client, serverMatch) }
            }
            .forEach { (client, server) ->
                resolvedAppointments.add(
                    resolve(client, server),
                )
            }

        return appointmentRepository.saveAllAndFlush(resolvedAppointments)
    }

    // This strategy is probably too simplistic for real world usage,
    // but good enough to get us going for now.
    private fun resolve(client: Appointment, server: Appointment): Appointment {
        return if (client.lastUpdated >= server.lastUpdated) {
            client
        } else {
            server
        }
    }
}
