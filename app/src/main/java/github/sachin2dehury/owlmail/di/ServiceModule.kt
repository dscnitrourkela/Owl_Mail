package github.sachin2dehury.owlmail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.services.AlarmBroadcast
import github.sachin2dehury.owlmail.services.NotificationExt
import github.sachin2dehury.owlmail.services.SyncBroadcastReceiver

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideNotificationExt(@ApplicationContext context: Context) = NotificationExt(context)

    @ServiceScoped
    @Provides
    fun provideAlarmBroadCast(@ApplicationContext context: Context) = AlarmBroadcast(context)

    @ServiceScoped
    @Provides
    fun provideSyncBroadcastReceiver() = SyncBroadcastReceiver()

}