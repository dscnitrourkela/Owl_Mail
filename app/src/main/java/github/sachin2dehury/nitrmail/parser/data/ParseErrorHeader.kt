package github.sachin2dehury.nitrmail.parser.data

data class ParseErrorHeader(
    override val name: String,
    override val value: String,
    val parseError: String
) : HeaderInterface
