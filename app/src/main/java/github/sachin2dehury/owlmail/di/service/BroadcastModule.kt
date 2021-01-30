package github.sachin2dehury.owlmail.di.service

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.services.AlarmBroadcast

@Module
@InstallIn(ServiceComponent::class)
object BroadcastModule {

    @ServiceScoped
    @Provides
    fun provideAlarmBroadCast(@ApplicationContext context: Context) = AlarmBroadcast(context)

}