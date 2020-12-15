package github.sachin2dehury.nitrmail.parser.util

import com.fasterxml.jackson.databind.ObjectMapper
import github.sachin2dehury.nitrmail.parser.data.ParsedMessage
import java.io.OutputStream

open class JacksonSerializer(private val mapper: ObjectMapper) : SerializerInterface {
    override fun writeToStream(message: ParsedMessage, outputStream: OutputStream) {
        mapper.writeValue(outputStream, message)
        outputStream.flush()
    }
}