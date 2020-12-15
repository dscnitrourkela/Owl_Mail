package github.sachin2dehury.nitrmail.parser.util

import github.sachin2dehury.nitrmail.parser.data.ParsedMessage
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

interface SerializerInterface {

    fun writeToStream(message: ParsedMessage, outputStream: OutputStream)

    fun writeToString(message: ParsedMessage): String {
        val outputStream = ByteArrayOutputStream()
        writeToStream(message, outputStream)
        return outputStream.toString(StandardCharsets.UTF_8.name())
    }

    fun writeToOutput(message: ParsedMessage) {
        writeToStream(message, System.out)
    }
}