package github.sachin2dehury.nitrmail.api.data.entities

import com.google.gson.annotations.SerializedName

data class Mails(
    @SerializedName("m") val mails: List<Mail> = emptyList(),
)