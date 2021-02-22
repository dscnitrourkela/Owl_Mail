package github.sachin2dehury.owlmail.api.data

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.text.SimpleDateFormat

@Entity(tableName = "parsed_mails")
data class ParsedMail(

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val conversationId: Int,
    val time: Long,
    val from: String?,
    val subject: String?,
    val to: String?,
    val cc: String?,
    val bcc: String?,
    val replyTo: String?,
    val body: String?,
    val attachments: String?
) {
    @SuppressLint("SimpleDateFormat")
    constructor(
        id: Int,
        conversationId: Int,
        mail: Document,
    ) : this(
        id, conversationId,
        SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm a").parse(
            mail.select(".MsgHdrSent").text()
        )?.time ?: 0,
        mail.select(".MsgHdrValue"),
        mail.select(".MsgBody")
    )

    internal constructor(
        id: Int, conversationId: Int, time: Long, header: Elements, frame: Elements
    ) : this(
        id,
        conversationId,
        time,
        header[0].text(),
        header[1].text(),
        header[2].text(),
        header[3].text(),
        header[4].text(),
        header[5].text(),
        frame.toString().substringBefore("<hr>"),
        frame.select("table tbody").toString()
    )
}
