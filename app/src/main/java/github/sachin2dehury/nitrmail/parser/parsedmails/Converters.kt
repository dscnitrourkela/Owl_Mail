package github.sachin2dehury.nitrmail.parser.parsedmails

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.sachin2dehury.nitrmail.parser.data.Address
import github.sachin2dehury.nitrmail.parser.data.HeaderInterface
import github.sachin2dehury.nitrmail.parser.data.MessageHint
import java.util.*

class Converters {

    @TypeConverter
    fun fromSender(address: Address): String {
        return Gson().toJson(address)
    }

    @TypeConverter
    fun toSender(string: String): Address {
        return Gson().fromJson(string, object : TypeToken<Address>() {}.type)
    }

    @TypeConverter
    fun fromListSender(address: List<Address>): String {
        return Gson().toJson(address)
    }

    @TypeConverter
    fun toListSender(string: String): List<Address> {
        return Gson().fromJson(string, object : TypeToken<List<Address>>() {}.type)
    }

    @TypeConverter
    fun fromListString(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toListString(string: String): List<String> {
        return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromListHeaderInterface(list: List<HeaderInterface>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toListHeaderInterface(string: String): List<HeaderInterface> {
        return Gson().fromJson(string, object : TypeToken<List<HeaderInterface>>() {}.type)
    }

    @TypeConverter
    fun fromListMessageHint(list: List<MessageHint>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toListMessageHint(string: String): List<MessageHint> {
        return Gson().fromJson(string, object : TypeToken<List<MessageHint>>() {}.type)
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return Gson().toJson(date)
    }

    @TypeConverter
    fun toDate(string: String): Date {
        return Gson().fromJson(string, object : TypeToken<Date>() {}.type)
    }
}