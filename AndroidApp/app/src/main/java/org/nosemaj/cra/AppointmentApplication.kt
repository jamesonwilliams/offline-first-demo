package org.nosemaj.cra

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import org.nosemaj.cra.data.sync.AppointmentSyncEngine
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AppointmentApplication : Application() {
    @Inject lateinit var syncEngine: AppointmentSyncEngine

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(syncEngine)
    }
}
