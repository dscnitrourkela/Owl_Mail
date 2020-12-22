package github.sachin2dehury.nitrmail.api.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.sachin2dehury.nitrmail.api.data.mails.Sender

class Converters {

    @TypeConverter
    fun fromList(list: List<Sender>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(string: String): List<Sender> {
        return Gson().fromJson(string, object : TypeToken<List<Sender>>() {}.type)
    }
}