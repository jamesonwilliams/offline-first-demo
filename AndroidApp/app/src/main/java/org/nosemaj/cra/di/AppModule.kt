package org.nosemaj.cra.di

import android.app.Application
import android.content.Context
import com.apollographql.apollo3.ApolloClient
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
    fun provideApolloClient(@ApplicationContext applicationContext: Context): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(applicationContext.getString(R.string.api_base_url))
            .build()
    }

    @Provides
    fun provideApplicationContext(application: Application): Context = application
}
