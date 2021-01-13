package github.sachin2dehury.nitrmail.api.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parsedMails")
data class ParsedMail(

    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val body: String = ""
)
