package github.sachin2dehury.nitrmail.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.sachin2dehury.nitrmail.adapters.MailBoxAdapter
import github.sachin2dehury.nitrmail.api.calls.AppClient
import github.sachin2dehury.nitrmail.api.data.MailDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppClient() = AppClient()

    @Provides
    @Singleton
    fun provideMailBoxAdapter() = MailBoxAdapter()

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, MailDatabase::class.java, "DATABASE_NAME")
        .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideRunDao(mailDatabase: MailDatabase) = mailDatabase.getMailDao()
}