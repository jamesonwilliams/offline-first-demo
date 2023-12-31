package org.nosemaj.cra.data.sync

import kotlinx.coroutines.flow.Flow

interface ConnectivityMonitor {
    // Emits a flow of boolean, true if the app is connected to the Internet, false otherwise.
    fun observeConnectivityChanges(): Flow<Boolean>
}
