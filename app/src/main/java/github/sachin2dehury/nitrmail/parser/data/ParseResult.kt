package github.sachin2dehury.nitrmail.parser.data

data class ParseResult(
    val mail: ParsedMail?,
    val exception: Exception?,
    val contextId: String
)
