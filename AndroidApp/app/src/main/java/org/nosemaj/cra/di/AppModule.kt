package org.nosemaj.cra.di

import android.app.Application
import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.nosemaj.cra.R

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
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

    @Provides
    fun provideApplicationContext(application: Application): Context = application
}
