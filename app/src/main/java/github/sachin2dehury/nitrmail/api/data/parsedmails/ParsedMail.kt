package github.sachin2dehury.nitrmail.api.data.parsedmails

import com.google.gson.annotations.SerializedName


data class ParsedMail(
    @SerializedName("date") val date: String = "",
    @SerializedName("from") val sender: Sender = Sender(),
    @SerializedName("html") val body: String = "",
    @SerializedName("subject") val subject: String = "",
)