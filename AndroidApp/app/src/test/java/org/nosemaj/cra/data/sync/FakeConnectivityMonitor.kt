package org.nosemaj.cra.data.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeConnectivityMonitor(
    private val connectivityFlow: Flow<Boolean> = flowOf(true),
) : ConnectivityMonitor {
    override fun observeConnectivityChanges() = connectivityFlow
}
