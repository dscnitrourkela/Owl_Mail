package github.sachin2dehury.nitrmail.api.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "mails")
data class Mail(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id") val id: String = "",

    @SerializedName("d") val time: Long = 0,
    @SerializedName("e") val senders: List<Sender> = emptyList(),
    @SerializedName("f") val flag: String = "",
    @SerializedName("fr") val body: String = "",
    @SerializedName("l") val folder: String = "",
//    2 inbox 3 trash 4 junk 5 sent 6 draft 7 contacts 10 calendar
    @SerializedName("su") val subject: String = "",

    var box: String = "",
)