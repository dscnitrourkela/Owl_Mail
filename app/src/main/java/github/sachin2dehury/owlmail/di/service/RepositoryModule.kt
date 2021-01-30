package github.sachin2dehury.owlmail.di.service

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.repository.DataStoreExt
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository

@Module
@InstallIn(ServiceComponent::class)
object RepositoryModule {

    @ServiceScoped
    @Provides
    fun provideMailRepository(
        @ApplicationContext context: Context,
        basicAuthInterceptor: BasicAuthInterceptor,
        mailApi: MailApi,
        mailDao: MailDao,
    ) = MailRepository(basicAuthInterceptor, context, mailApi, mailDao)

    @ServiceScoped
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext context: Context,
        dataStore: DataStoreExt,
    ) = DataStoreRepository(context, dataStore)
}