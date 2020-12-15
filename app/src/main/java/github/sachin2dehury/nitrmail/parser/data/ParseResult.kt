package github.sachin2dehury.nitrmail.parser.data

import github.sachin2dehury.nitrmail.parser.data.ParsedMessage

data class ParseResult(
    val message: ParsedMessage?,
    val exception: Exception?,
    val contextId: String
)
