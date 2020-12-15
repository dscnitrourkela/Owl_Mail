package github.sachin2dehury.nitrmail.parser.data

import java.util.*
import kotlin.math.ln
import kotlin.math.pow

data class ParsedMessage(
    val subject: String,
    val messageId: String? = null,
    val from: Address? = null,
    val sender: Address? = null,
    val replyTo: List<Address>,
    val returnPath: Address? = null,
    val tos: List<Address>,
    val ccs: List<Address>,
    val date: Date?,
    val references: List<String>,
    val bodyText: String? = null,
    val bodyHtml: String? = null,
    val headers: List<HeaderInterface>,
    val hints: List<MessageHint>,
    val size: Int = 0
) {
    fun getReadableSize(si: Boolean = true): String {
        val unit = if (si) 1000 else 1024
        if (size < unit) return "$size B"
        val exp = (ln(size.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format("%.1f %sB", size / unit.toDouble().pow(exp.toDouble()), pre)
    }
}
