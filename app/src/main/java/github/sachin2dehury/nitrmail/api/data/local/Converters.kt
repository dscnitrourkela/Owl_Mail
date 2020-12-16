package github.sachin2dehury.nitrmail.api.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.sachin2dehury.nitrmail.api.data.entities.Mail

class Converters {
    @TypeConverter
    fun fromList(list: List<Mail>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(string: String): List<Mail> {
        return Gson().fromJson(string, object : TypeToken<List<Mail>>() {}.type)
    }
}