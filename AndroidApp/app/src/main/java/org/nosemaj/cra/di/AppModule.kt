package org.nosemaj.cra.di

import android.content.Context
import androidx.room.Room
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.nosemaj.cra.R
import org.nosemaj.cra.data.db.AppDatabase
import org.nosemaj.cra.data.db.AppointmentDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideApolloClient(@ApplicationContext applicationContext: Context): ApolloClient =
        ApolloClient.Builder()
            .subscriptionNetworkTransport(
                WebSocketNetworkTransport.Builder()
                    .protocol(GraphQLWsProtocol.Factory())
                    .serverUrl(applicationContext.getString(R.string.gql_subscription_base_url))
                    .build()
            )
            .serverUrl(applicationContext.getString(R.string.gql_base_url))
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase::class.simpleName,
        ).build()
    }

    @Provides
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao = database.appointmentDao()
}

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {
    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
}