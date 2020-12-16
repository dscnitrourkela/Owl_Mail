package github.sachin2dehury.nitrmail.parser.unused

import github.sachin2dehury.nitrmail.parser.data.ParsedMail
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

interface SerializerInterface {

    fun writeToStream(mail: ParsedMail, outputStream: OutputStream)

    fun writeToString(mail: ParsedMail): String {
        val outputStream = ByteArrayOutputStream()
        writeToStream(mail, outputStream)
        return outputStream.toString(StandardCharsets.UTF_8.name())
    }

    fun writeToOutput(mail: ParsedMail) {
        writeToStream(mail, System.out)
    }
}