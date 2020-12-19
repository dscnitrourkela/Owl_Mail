package github.sachin2dehury.nitrmail.api.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "mails")
data class Mail(
    @SerializedName("d") val time: Long = 0,
    @SerializedName("e") val senders: List<Sender> = emptyList(),
    @SerializedName("f") val isUnread: String = "",
    @SerializedName("fr") val body: String = "",
    @SerializedName("su") val subject: String = "",

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id") val id: String = ""
)