package github.sachin2dehury.owlmail.api.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.sachin2dehury.owlmail.api.data.Address

class Converters {

    @TypeConverter
    fun fromList(list: List<Address>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(string: String): List<Address> {
        return Gson().fromJson(string, object : TypeToken<List<Address>>() {}.type)
    }
}