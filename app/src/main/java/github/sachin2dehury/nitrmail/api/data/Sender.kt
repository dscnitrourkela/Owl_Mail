package github.sachin2dehury.nitrmail.api.data

import com.squareup.moshi.Json

data class Sender(
    @field:Json(name = "a") val address: String = "",
    @field:Json(name = "d") val firstName: String = "",
    @field:Json(name = "p") val name: String = "",
    @field:Json(name = "t") val isReceiver: String = ""
)