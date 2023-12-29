package org.nosemaj.cra.data.sync

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.nosemaj.cra.data.AppointmentModel
import org.nosemaj.cra.data.db.AppointmentDao
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentSyncEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val appointmentDao: AppointmentDao,
    private val networkAppointmentDataSource: NetworkAppointmentDataSource,
): DefaultLifecycleObserver {
    private var job: Job? = null

    override fun onResume(owner: LifecycleOwner) {
        monitorNetworkState()
    }

    private fun monitorNetworkState() {
        job?.cancel()
        job = context.networkStateFlow()
            .filter { it }
            .flatMapLatest {
                Timber.v("Network came back... syncing data.")
                syncData()
                    .catch {
                        Timber.w(it, "Sync flow exited.")
                        monitorNetworkState()
                    }
            }
            .launchIn(coroutineScope)
    }

    private fun syncData(): Flow<List<AppointmentModel>> {
        return appointmentDao.observeAll().flatMapLatest { localAppointments ->
            networkAppointmentDataSource.observeAppointments(localAppointments)
        }
            .distinctUntilChanged()
            .onEach { latestAppointments ->
                Timber.v("Saving data from network.")
                appointmentDao.upsertAll(latestAppointments)
            }
    }

    override fun onPause(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }
}
