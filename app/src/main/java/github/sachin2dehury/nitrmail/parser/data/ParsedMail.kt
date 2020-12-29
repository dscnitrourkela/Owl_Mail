package github.sachin2dehury.nitrmail.parser.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "parsed")
data class ParsedMail(
    val subject: String = "",
    val messageId: String = "",
    val from: Address = Address(),
    val sender: Address = Address(),
    val replyTo: List<Address> = emptyList(),
    val returnPath: Address = Address(),
    val tos: List<Address> = emptyList(),
    val ccs: List<Address> = emptyList(),
    val date: Date = Date(),
    val references: List<String> = emptyList(),
    val bodyText: String = "",
    val bodyHtml: String = "",
//    val headers: List<HeaderInterface> = emptyList(),
    val hints: List<MessageHint> = emptyList(),

    @PrimaryKey(autoGenerate = false)
    var id: String = ""
)
