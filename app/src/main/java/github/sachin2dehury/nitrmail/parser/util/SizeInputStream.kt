package github.sachin2dehury.nitrmail.parser.util

import java.io.InputStream

class SizeInputStream(private val inputStream: InputStream) : InputStream() {

    var bytesRead = 0
        private set

    override fun read(): Int {
        val read = inputStream.read()
        if (read == -1) {
            return -1
        }

        bytesRead += 1
        return read
    }

    override fun read(bytes: ByteArray?): Int {
        val read = inputStream.read(bytes)
        bytesRead += read
        return read
    }

    override fun read(bytes: ByteArray?, off: Int, len: Int): Int {
        val read = inputStream.read(bytes, off, len)
        bytesRead += read
        return read
    }
}