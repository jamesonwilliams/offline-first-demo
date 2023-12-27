package org.nosemaj.cra.repository

import org.nosemaj.cra.entity.Appointment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AppointmentRepository : JpaRepository<Appointment, UUID>
