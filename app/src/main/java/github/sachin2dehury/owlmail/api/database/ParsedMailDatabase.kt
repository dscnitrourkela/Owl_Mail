package github.sachin2dehury.owlmail.api.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.sachin2dehury.owlmail.api.data.ParsedMail

@Database(
    entities = [ParsedMail::class],
    version = 1
)

@TypeConverters(ParsedMailConverters::class)
abstract class ParsedMailDatabase : RoomDatabase() {
    abstract fun getParsedMailDao(): ParsedMailDao
}