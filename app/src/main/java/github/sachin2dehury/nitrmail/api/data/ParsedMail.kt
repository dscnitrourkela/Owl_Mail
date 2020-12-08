package github.sachin2dehury.nitrmail.api.data

data class ParsedMail(
    val sender: String,
    val date: String,
    val subject: String,
    val contentType: String,
    val charSet: String,
    val encoding: String,
    val messageID: String,
    val messageBody: String
)