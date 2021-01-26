package github.sachin2dehury.owlmail.api.data

import com.google.gson.annotations.SerializedName

data class Mails(
    @SerializedName("m") val mails: List<Mail> = emptyList(),
)