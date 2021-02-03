package github.sachin2dehury.owlmail.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.sachin2dehury.owlmail.api.database.MailDatabase
import github.sachin2dehury.owlmail.others.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMailDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, MailDatabase::class.java, Constants.DATABASE_NAME)
        .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideMailDao(mailDatabase: MailDatabase) = mailDatabase.getMailDao()
}