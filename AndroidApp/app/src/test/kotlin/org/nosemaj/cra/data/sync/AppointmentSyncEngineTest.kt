package org.nosemaj.cra.data.sync

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.nosemaj.cra.data.TestAppointments
import org.nosemaj.cra.data.db.FakeAppointmentDao
import org.nosemaj.cra.data.net.FakeNetworkAppointmentDataSource

class AppointmentSyncEngineTest {
    private lateinit var testScope: TestScope
    private lateinit var connectivityMonitor: ConnectivityMonitor
    private lateinit var fakeAppointmentDao: FakeAppointmentDao
    private lateinit var fakeNetworkSource: FakeNetworkAppointmentDataSource
    private lateinit var syncEngine: AppointmentSyncEngine

    @Before
    fun setup() {
        connectivityMonitor = FakeConnectivityMonitor()
        testScope = TestScope()
        fakeAppointmentDao = FakeAppointmentDao()
        fakeNetworkSource = FakeNetworkAppointmentDataSource()
        syncEngine = AppointmentSyncEngine(
            connectivityMonitor = connectivityMonitor,
            coroutineScope = testScope,
            appointmentDao = fakeAppointmentDao,
            networkAppointmentDataSource = fakeNetworkSource
        )
    }

    @Test
    fun `sync engine pulls remote state into local db`() = testScope.runTest {
        // Arrange client and server state
        val appointments = TestAppointments.randomAppointments(4)
        val clientOnly = appointments.subList(0, 2)
        val serverOnly = appointments.subList(2, 4)
        fakeAppointmentDao.upsertAll(clientOnly)
        fakeNetworkSource.remoteAppointments.addAll(serverOnly)

        // Act: sync occurs
        syncEngine.syncData().first()

        // Assert: both are in the local database, now.
        assertEquals(
            (clientOnly + serverOnly).sortedBy { it.startTime },
            fakeAppointmentDao.getAll()
        )
    }
}
