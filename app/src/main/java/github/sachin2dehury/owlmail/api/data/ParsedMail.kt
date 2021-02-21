package github.sachin2dehury.owlmail.api.data

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat

@Entity(tableName = "parsed_mails")
data class ParsedMail(

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val conversationId: Int,
    val time: Long,
    val from: String?,
    val to: String?,
    val body: String?,
    val subject: String?,
    val attachments: List<String>
) {
    @SuppressLint("SimpleDateFormat")
    constructor(
        id: Int,
        conversationId: Int,
        mail: Document,
    ) : this(
        id,
        conversationId,
        SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm a").parse(
            mail.select(".small-gray-text").text()
        )?.time ?: 0,
        mail.select("#d_from").text(),
        mail.select("#d_div").text().trim().replace(',', '\n').replace("Cc:", "\nCc:"),
        mail.select("#iframeBody").html(),
        mail.select(".zo_unread").text(),
        mail.select(".View.attachments").text().trim().split(") ")
    )
}
