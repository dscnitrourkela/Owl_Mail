package github.sachin2dehury.nitrmail.api.data.parsedmails

import com.google.gson.annotations.SerializedName

data class Sender(
    @SerializedName("email") val email: String = "",
    @SerializedName("name") val name: String = ""
)