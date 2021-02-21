package github.sachin2dehury.owlmail.api.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.sachin2dehury.owlmail.api.data.Address

class MailConverters {

    @TypeConverter
    fun fromList(value: List<Address>): String = Gson().toJson(value)

    @TypeConverter
    fun toList(value: String): List<Address> =
        Gson().fromJson(value, object : TypeToken<List<Address>>() {}.type)
}