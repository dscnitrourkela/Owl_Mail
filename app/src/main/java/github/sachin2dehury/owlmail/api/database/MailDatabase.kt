package github.sachin2dehury.owlmail.api.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.api.data.ParsedMail

@Database(
    entities = [Mail::class, ParsedMail::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class MailDatabase : RoomDatabase() {
    abstract fun getMailDao(): MailDao
    abstract fun getParsedMailDao(): ParsedMailDao
}