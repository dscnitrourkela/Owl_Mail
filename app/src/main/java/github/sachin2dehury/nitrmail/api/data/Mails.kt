package github.sachin2dehury.nitrmail.api.data

import com.squareup.moshi.Json

data class Mails(
    @field:Json(name = "m") val mails: List<Mail> = emptyList()
)