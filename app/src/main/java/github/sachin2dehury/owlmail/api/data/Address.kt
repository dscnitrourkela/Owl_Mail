package github.sachin2dehury.owlmail.api.data

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("a") val email: String = "",
    @SerializedName("d") val firstName: String = "",
    @SerializedName("p") val name: String = "",
    @SerializedName("t") val isReceiver: String = ""
)