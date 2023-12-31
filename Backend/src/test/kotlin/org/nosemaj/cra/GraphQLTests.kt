package org.nosemaj.cra

import org.junit.jupiter.api.Test
import org.nosemaj.cra.entity.AppointmentStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.graphql.test.tester.WebGraphQlTester
import org.springframework.graphql.test.tester.WebSocketGraphQlTester
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.test.StepVerifier
import java.net.URI
import java.util.UUID

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
class GraphQLTests {
    @Autowired
    private lateinit var webGraphQlTester: WebGraphQlTester

    @LocalServerPort
    private var localPort: Int? = null

    @Test
    fun addModifyAndList() {
        // Add
        val appointmentId =
            webGraphQlTester
                .document(
                    """
                    mutation {
                      addAppointment(
                        patientName: "Greggory Sampson"
                        startTime: "2024-03-15T08:30:00-06:00"
                        endTime: "2024-03-15T09:15:00-06:00"
                      ) {
                        id
                      }
                    }
                    """,
                )
                .execute()
                .path("addAppointment.id")
                .entity(UUID::class.java)
                .get()

        // Modify
        webGraphQlTester
            .document(
                """
                mutation {
                  modifyAppointment(id: "$appointmentId", status: Recording) {
                    status
                  }
                }
                """,
            )
            .execute()
            .path("modifyAppointment.status")
            .entity(AppointmentStatus::class.java)
            .isEqualTo(AppointmentStatus.Recording)

        // List
        webGraphQlTester
            .document(
                """
                {
                  listAppointments {
                    patientName
                  }
                }
                """,
            )
            .execute()
            .path("listAppointments[*].patientName")
            .entityList(String::class.java)
            .contains("Greggory Sampson")
    }

    @Test
    fun subscribeAndAdd() {
        // Arrange a subscription
        val webClient = ReactorNettyWebSocketClient()
        val uri = URI.create("http://localhost:${localPort!!}/graphql")
        val webSocketGraphQlTester = WebSocketGraphQlTester.create(uri, webClient)
        val greetingFlux =
            webSocketGraphQlTester
                .document(
                    """
                    subscription {
                      appointmentChange {
                        patientName
                      }
                    }
                    """,
                )
                .executeSubscription()
                .toFlux("appointmentChange.patientName", String::class.java)

        // Create an appointment
        webGraphQlTester
            .document(
                """
                mutation {
                  addAppointment(
                    patientName: "Tobias Danielssen"
                    startTime: "2024-01-30T19:30:00-06:00"
                    endTime: "2024-01-30T20:15:00-06:00"
                  ) {
                    id
                  }
                }
                """,
            )
            .execute()

        // Expect a subscription event
        StepVerifier
            .create(greetingFlux)
            .expectNext("Tobias Danielssen")
    }
}
