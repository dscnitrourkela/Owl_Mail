package github.sachin2dehury.nitrmail.api.databases.parsedmails

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.sachin2dehury.nitrmail.api.data.parsedmails.Sender

class Converters {

    @TypeConverter
    fun fromSender(sender: Sender): String {
        return Gson().toJson(sender)
    }

    @TypeConverter
    fun toSender(string: String): Sender {
        return Gson().fromJson(string, object : TypeToken<Sender>() {}.type)
    }
}