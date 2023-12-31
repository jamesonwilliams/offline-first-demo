package org.nosemaj.cra.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.nosemaj.cra.data.AppointmentModel.AppointmentStatus
import org.nosemaj.cra.data.db.FakeAppointmentDao
import org.nosemaj.cra.data.net.FakeNetworkAppointmentDataSource
import org.nosemaj.cra.util.FakeTimeProvider

class AppointmentRepositoryTest {
    private lateinit var dao: FakeAppointmentDao
    private lateinit var networkSource: FakeNetworkAppointmentDataSource
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var repo: AppointmentRepository

    @Before
    fun setup() {
        dao = FakeAppointmentDao()
        networkSource = FakeNetworkAppointmentDataSource()
        timeProvider = FakeTimeProvider()
        repo = DefaultAppointmentRepository(dao, networkSource, timeProvider)
    }

    @Test
    fun updateAndGetAppointments() = runTest {
        // Arrange some data into the database
        val testAppointments = TestAppointments
            .randomAppointments(2)
            .map { it.copy(status = AppointmentStatus.Scheduled) }
        dao.upsertAll(testAppointments)

        // Act by updating statuses, getting the list back
        testAppointments.map {
            repo.updateStatus(it.id, AppointmentStatus.Cancelled)
        }
        val currentAppointments = repo.getAppointments().first()

        // Assert that the returned appointments are the original appointments,
        // but with their statuses updated.
        assertEquals(2, currentAppointments.size)
        assertEquals(
            testAppointments.map {
                it.copy(
                    status = AppointmentStatus.Cancelled,
                    lastUpdated = timeProvider.now()
                )
            },
            currentAppointments
        )

        // Assert that the remote data source has been updated with the new statuses.
        assertEquals(
            networkSource.remoteAppointments,
            currentAppointments
        )
    }
}
