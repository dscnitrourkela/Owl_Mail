package github.sachin2dehury.owlmail.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMailRepository(
        @ApplicationContext context: Context,
        basicAuthInterceptor: BasicAuthInterceptor,
        mailApi: MailApi,
        mailDao: MailDao,
    ) = MailRepository(basicAuthInterceptor, context, mailApi, mailDao)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) =
        context.createDataStore(Constants.DATA_STORE_NAME)

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ) = DataStoreRepository(context, dataStore)
}