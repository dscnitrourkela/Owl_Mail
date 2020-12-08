package github.sachin2dehury.nitrmail.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import github.sachin2dehury.nitrmail.adapters.MailBoxAdapter
import github.sachin2dehury.nitrmail.api.calls.AppClient
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppClient() = AppClient()

    @Provides
    @Singleton
    fun provideMailBoxAdapter() = MailBoxAdapter()
}