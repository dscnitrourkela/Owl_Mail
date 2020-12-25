package github.sachin2dehury.nitrmail.api.data.parsedmails

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "parsed")
data class ParsedMail(
    @SerializedName("date") val date: String = "",
    @SerializedName("from") val sender: Sender = Sender(),
    @SerializedName("html") val body: String = "",
    @SerializedName("subject") val subject: String = "",

    @PrimaryKey(autoGenerate = false)
    var id: String = ""
)