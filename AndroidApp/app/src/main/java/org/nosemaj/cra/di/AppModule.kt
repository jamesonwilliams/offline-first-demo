package org.nosemaj.cra.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nosemaj.cra.data.net.AppointmentService

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesAppointmentService(): AppointmentService {
        return AppointmentService.create()
    }

    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application
    }
}