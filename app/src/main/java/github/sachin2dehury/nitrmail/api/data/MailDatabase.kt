package github.sachin2dehury.nitrmail.api.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Mail::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class MailDatabase : RoomDatabase() {

    abstract fun getMailDao(): MailDao
}