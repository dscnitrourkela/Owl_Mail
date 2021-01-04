package github.sachin2dehury.nitrmail.api.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "mails")
data class Mail(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id") val id: String = "",

    @SerializedName("cid") val conversationId: String = "",
    @SerializedName("d") val time: Long = 0,
    @SerializedName("e") val addresses: List<Address> = emptyList(),
    @SerializedName("f") val flag: String = "",
    @SerializedName("fr") val body: String = "",
    @SerializedName("l") val box: String = "",
//    @SerializedName("rev") val rev: Int = 0,
    @SerializedName("s") val size: Int = 0,
    @SerializedName("su") val subject: String = "",

    var html: String = "",
//    var attachments: Int = 0,
)