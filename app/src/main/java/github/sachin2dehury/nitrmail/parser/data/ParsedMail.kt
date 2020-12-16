package github.sachin2dehury.nitrmail.parser.data

import java.util.*

data class ParsedMail(
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
)
