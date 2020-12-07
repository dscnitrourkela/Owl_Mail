package github.sachin2dehury.nitrmail.api.data

import com.squareup.moshi.Json

data class Mail(
    @field:Json(name = "d") val time: Long = 0,
    @field:Json(name = "e") val sender: List<Sender> = emptyList(),
    @field:Json(name = "f") val isUnread: String = "",
    @field:Json(name = "fr") val body: String = "",
    @field:Json(name = "id") val id: String = "",
    @field:Json(name = "su") val subject: String = ""
)