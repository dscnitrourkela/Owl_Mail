package github.sachin2dehury.nitrmail.api.databases.mails

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.sachin2dehury.nitrmail.api.data.mails.Mail

@Database(
    entities = [Mail::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class MailDatabase : RoomDatabase() {

    abstract fun getMailDao(): MailDao
}