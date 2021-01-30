package github.sachin2dehury.owlmail.di.service

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import github.sachin2dehury.owlmail.services.AlarmBroadcast
import github.sachin2dehury.owlmail.services.NotificationExt
import github.sachin2dehury.owlmail.services.SyncWorker

@Module
@InstallIn(ServiceComponent::class)
object SyncModule {

    @ServiceScoped
    @Provides
    fun provideSyncWorker(
        alarmBroadcast: AlarmBroadcast,
        dataStoreRepository: DataStoreRepository,
        mailRepository: MailRepository,
        notificationExt: NotificationExt,
    ) = SyncWorker(alarmBroadcast, dataStoreRepository, mailRepository, notificationExt)
}