package github.sachin2dehury.owlmail.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import github.sachin2dehury.owlmail.services.AlarmBroadcast

@Module
@InstallIn(ActivityRetainedComponent::class)
object BroadcastModule {

    @ActivityRetainedScoped
    @Provides
    fun provideAlarmBroadCast(@ApplicationContext context: Context) = AlarmBroadcast(context)

}