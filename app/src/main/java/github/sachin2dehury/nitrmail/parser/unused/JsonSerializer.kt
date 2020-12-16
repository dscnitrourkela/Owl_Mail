package github.sachin2dehury.nitrmail.parser.unused

import android.annotation.SuppressLint
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import github.sachin2dehury.nitrmail.others.Constants
import java.text.SimpleDateFormat

class JsonSerializer(mapper: ObjectMapper) : JacksonSerializer(mapper) {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun create(): JsonSerializer {
            val om = ObjectMapper()
            om.enable(SerializationFeature.INDENT_OUTPUT)
            om.dateFormat = SimpleDateFormat(Constants.DATE_FORMAT)
            return JsonSerializer(om)
        }
    }
}