package org.nosemaj.cra.data.sync

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import okio.IOException
import org.nosemaj.cra.data.AppointmentModel
import org.nosemaj.cra.data.db.AppointmentDao
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class AppointmentSyncEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val appointmentDao: AppointmentDao,
    private val networkAppointmentDataSource: NetworkAppointmentDataSource,
): DefaultLifecycleObserver {
    private var job: Job? = null

    override fun onResume(owner: LifecycleOwner) {
        job?.cancel()
        job = context.networkStateFlow()
            .filter { it }
            .flatMapLatest {
                syncData(appointmentDao.getAll())
            }
            .launchIn(coroutineScope)
    }

    private fun syncData(appointments: List<AppointmentModel>): Flow<Unit> {
        Timber.v("Syncing local appointments with remote host...")
        return networkAppointmentDataSource.observeAppointments(appointments)
            .retryWhen { cause, attempt ->
                delay(1.seconds)
                cause.cause is IOException && attempt < 3
            }
            .catch { Timber.w(it, "Failure while syncing state!") }
            .map { updates ->
                Timber.v("Got remote state, applying locally.")
                appointmentDao.upsertAll(updates)
            }
    }

    override fun onPause(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }
}
