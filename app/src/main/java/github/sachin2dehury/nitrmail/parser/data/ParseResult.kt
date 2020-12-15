package github.sachin2dehury.nitrmail.parser.data

data class ParseResult(
    val message: ParsedMessage?,
    val exception: Exception?,
    val contextId: String
)
