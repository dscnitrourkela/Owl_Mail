package github.sachin2dehury.owlmail.api.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.sachin2dehury.owlmail.api.data.Mail

@Database(
    entities = [Mail::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class MailDatabase : RoomDatabase() {

    abstract fun getMailDao(): MailDao
}