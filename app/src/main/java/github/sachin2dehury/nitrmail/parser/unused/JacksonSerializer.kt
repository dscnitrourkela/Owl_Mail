package github.sachin2dehury.nitrmail.parser.unused

import com.fasterxml.jackson.databind.ObjectMapper
import github.sachin2dehury.nitrmail.parser.data.ParsedMail
import java.io.OutputStream

open class JacksonSerializer(private val mapper: ObjectMapper) : SerializerInterface {
    override fun writeToStream(mail: ParsedMail, outputStream: OutputStream) {
        mapper.writeValue(outputStream, mail)
        outputStream.flush()
    }
}