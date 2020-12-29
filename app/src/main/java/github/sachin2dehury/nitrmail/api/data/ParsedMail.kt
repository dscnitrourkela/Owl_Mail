package github.sachin2dehury.nitrmail.api.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parsed")
data class ParsedMail(
    val response: String = "",

    @PrimaryKey(autoGenerate = false)
    var id: String = ""
)
