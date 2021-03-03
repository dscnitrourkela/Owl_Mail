package github.sachin2dehury.owlmail.api.data

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

@Entity(tableName = "parsed_mails")
data class ParsedMail(

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val conversationId: Int,
    val time: String?,
    val from: String?,
    val subject: String?,
    val address: List<String>?,
    val body: String?,
    val attachmentsName: List<String>?,
    val attachmentsLink: List<String>?,
) {

    @SuppressLint("SimpleDateFormat")
    constructor(
        id: Int,
        conversationId: Int,
        mail: Document,
    ) : this(
        id,
        conversationId,
        mail.select(".small-gray-text").text(),
        mail.select("#d_from").text(),
        mail.select(".zo_unread").text(),
        mail.select("#d_div .View.address").eachText(),
        mail.select(".msgwrap").toString(),
        mail.select(".View.attachments")
    )

    constructor(
        id: Int,
        conversationId: Int,
        time: String,
        from: String?,
        subject: String?,
        address: List<String>?,
        body: String?,
        attachments: Elements?,
    ) : this(
        id,
        conversationId,
        time,
        from,
        subject,
        address,
        body,
        attachments?.eachText(),
        attachments?.eachAttr("[href]"),
    )
}
