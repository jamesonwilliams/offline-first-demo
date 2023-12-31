package org.nosemaj.cra.di

import android.content.Context
import androidx.room.Room
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import org.nosemaj.cra.R
import org.nosemaj.cra.data.AppointmentRepository
import org.nosemaj.cra.data.DefaultAppointmentRepository
import org.nosemaj.cra.data.db.AppDatabase
import org.nosemaj.cra.data.db.AppointmentDao
import org.nosemaj.cra.data.net.GqlNetworkAppointmentDataSource
import org.nosemaj.cra.data.net.NetworkAppointmentDataSource
import org.nosemaj.cra.data.sync.AndroidConnectivityMonitor
import org.nosemaj.cra.data.sync.ConnectivityMonitor
import org.nosemaj.cra.util.DefaultTimeProvider
import org.nosemaj.cra.util.TimeProvider

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideApolloClient(@ApplicationContext applicationContext: Context): ApolloClient {
        return ApolloClient.Builder()
            .subscriptionNetworkTransport(
                WebSocketNetworkTransport.Builder()
                    .protocol(GraphQLWsProtocol.Factory())
                    .okHttpClient(
                        OkHttpClient.Builder()
                            .pingInterval(2, TimeUnit.SECONDS)
                            .build()
                    )
                    .serverUrl(applicationContext.getString(R.string.gql_subscription_base_url))
                    .build()
            )
            .httpEngine(DefaultHttpEngine(timeoutMillis = 2_000))
            .serverUrl(applicationContext.getString(R.string.gql_base_url))
            .build()
    }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase::class.simpleName
        ).build()
    }

    @Singleton
    @Provides
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao = database.appointmentDao()

    @InstallIn(SingletonComponent::class)
    @Module
    abstract class Bindings {
        @Binds
        abstract fun bindNetworkAppointmentDataSource(
            gqlNetworkAppointmentDataSource: GqlNetworkAppointmentDataSource,
        ): NetworkAppointmentDataSource

        @Binds
        abstract fun bindTimeProvider(defaultTimeProvider: DefaultTimeProvider): TimeProvider

        @Binds
        abstract fun bindConnectivityMonitor(
            androidConnectivityMonitor: AndroidConnectivityMonitor,
        ): ConnectivityMonitor

        @Binds
        abstract fun bindAppointmentRepository(
            defaultAppointmentRepository: DefaultAppointmentRepository,
        ): AppointmentRepository
    }
}
