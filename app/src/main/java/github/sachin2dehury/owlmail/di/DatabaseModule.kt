package github.sachin2dehury.owlmail.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.sachin2dehury.owlmail.api.database.MailDatabase
import github.sachin2dehury.owlmail.api.database.ParsedMailDatabase
import github.sachin2dehury.owlmail.others.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMailDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, MailDatabase::class.java, Constants.MAIL_DATABASE)
        .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideMailDao(mailDatabase: MailDatabase) = mailDatabase.getMailDao()

    @Singleton
    @Provides
    fun provideParsedMailDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        ParsedMailDatabase::class.java,
        Constants.PARSED_MAIL_DATABASE
    )
        .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideParsedMailDao(parsedMailDatabase: ParsedMailDatabase) =
        parsedMailDatabase.getParsedMailDao()
}