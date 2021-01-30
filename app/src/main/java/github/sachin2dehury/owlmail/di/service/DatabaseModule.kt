package github.sachin2dehury.owlmail.di.service

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.api.database.MailDatabase
import github.sachin2dehury.owlmail.others.Constants

@Module
@InstallIn(ServiceComponent::class)
object DatabaseModule {

    @ServiceScoped
    @Provides
    fun provideMailDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, MailDatabase::class.java, Constants.DATABASE_NAME)
        .fallbackToDestructiveMigration().build()

    @ServiceScoped
    @Provides
    fun provideMailDao(mailDatabase: MailDatabase) = mailDatabase.getMailDao()
}