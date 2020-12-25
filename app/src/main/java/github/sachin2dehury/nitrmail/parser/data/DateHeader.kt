package github.sachin2dehury.nitrmail.parser.data

import java.util.*

data class DateHeader(
    override val name: String,
    override val value: String,
    val date: Date
) : HeaderInterface