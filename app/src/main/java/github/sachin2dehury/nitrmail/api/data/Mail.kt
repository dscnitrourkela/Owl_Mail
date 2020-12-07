package github.sachin2dehury.nitrmail.api.data

import com.squareup.moshi.Json

data class Mail(
    @field:Json(name = "d") val time: Long = 0, //d
    @field:Json(name = "e") val sender: List<Sender> = emptyList(), //e
    @field:Json(name = "fr") val body: String = "", //fr
    @field:Json(name = "id") val id: String = "",
    @field:Json(name = "su") val subject: String = "" //su
)