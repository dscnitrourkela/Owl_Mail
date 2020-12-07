package github.sachin2dehury.nitrmail.api.data

import com.squareup.moshi.Json

data class Sender(
    @field:Json(name = "a") val address: String = "", //a
    @field:Json(name = "d") val name: String = "", //d
    @field:Json(name = "p") val company: String = "", //p
    @field:Json(name = "t") val isReceiver: String = "f" //t
)