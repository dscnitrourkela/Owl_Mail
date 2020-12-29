package github.sachin2dehury.nitrmail.api.database

import androidx.room.Database
import androidx.room.RoomDatabase
import github.sachin2dehury.nitrmail.api.data.ParsedMail

@Database(
    entities = [ParsedMail::class],
    version = 1
)

abstract class ParsedMailDatabase : RoomDatabase() {

    abstract fun getParsedMailDao(): ParsedMailDao
}