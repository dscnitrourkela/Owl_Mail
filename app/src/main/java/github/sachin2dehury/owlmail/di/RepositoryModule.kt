package github.sachin2dehury.owlmail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.repository.DataStoreExt
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
    fun provideDataStoreRepository(
        @ApplicationContext context: Context,
        dataStore: DataStoreExt,
    ) = DataStoreRepository(context, dataStore)
}