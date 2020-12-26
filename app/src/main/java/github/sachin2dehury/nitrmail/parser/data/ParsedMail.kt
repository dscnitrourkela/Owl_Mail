package github.sachin2dehury.nitrmail.parser.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "parsed")
data class ParsedMail(
    val subject: String = "",
    val from: Address = Address(),
    val date: Date = Date(),
    val bodyText: String = "",
    val bodyHtml: String = "",

    @PrimaryKey(autoGenerate = false)
    var id: String = ""
)
