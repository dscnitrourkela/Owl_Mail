package github.sachin2dehury.nitrmail.parser.parsedmails

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.sachin2dehury.nitrmail.parser.data.ParsedMail

@Database(
    entities = [ParsedMail::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class ParsedMailDatabase : RoomDatabase() {

    abstract fun getParsedMailDao(): ParsedMailDao
}