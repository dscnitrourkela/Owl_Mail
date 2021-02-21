package github.sachin2dehury.owlmail.api.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ParsedMailConverters {

    @TypeConverter
    fun fromList(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun toList(value: String): List<String> =
        Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
}