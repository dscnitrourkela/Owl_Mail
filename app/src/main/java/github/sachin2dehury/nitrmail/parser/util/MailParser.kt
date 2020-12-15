package github.sachin2dehury.nitrmail.parser.util

import github.sachin2dehury.nitrmail.parser.data.Address
import github.sachin2dehury.nitrmail.parser.data.MessageHint
import github.sachin2dehury.nitrmail.parser.data.ParseResult
import github.sachin2dehury.nitrmail.parser.data.ParsedMessage
import org.apache.commons.io.IOUtils
import org.apache.james.mime4j.dom.MessageBuilder
import org.apache.james.mime4j.dom.TextBody
import org.apache.james.mime4j.field.LenientFieldParser
import org.apache.james.mime4j.field.MailboxFieldLenientImpl
import org.apache.james.mime4j.message.DefaultMessageBuilder
import java.io.InputStream
import java.util.*

class MailParser {

    companion object {
        fun createDefaultMessageBuilder(): MessageBuilder {
            val fieldParser = LenientFieldParser()
            fieldParser.setFieldParser("Return-Path", MailboxFieldLenientImpl.PARSER)

            val messageBuilder = DefaultMessageBuilder()
            messageBuilder.setFieldParser(fieldParser)
            return messageBuilder
        }
    }

    fun parse(inputStream: InputStream): ParsedMessage =
        parse(inputStream, createDefaultMessageBuilder())

    fun parse(inputStream: InputStream, msgBuilder: MessageBuilder): ParsedMessage {
        val sizeInputStream = SizeInputStream(inputStream)
        val parsedMessage = msgBuilder.parseMessage(sizeInputStream)

        val messageId = parsedMessage.messageId ?: null

        val subject = parsedMessage.subject ?: ""
        val fromAddress: Address = parsedMessage.from?.toAddressList()!!.first()
        val senderAddress: Address? = parsedMessage.sender?.toAddress()
        val replyToAddresses: List<Address> = parsedMessage.replyTo?.toAddressList() ?: listOf()
        val toAddresses: List<Address> = parsedMessage.to?.toAddressList() ?: listOf()
        val ccAddresses: List<Address> = parsedMessage.cc?.toAddressList() ?: listOf()

        val returnPathField = parsedMessage.header.getField("Return-Path")
        val returnPath = if (returnPathField != null) getReturnPathAddr(returnPathField) else null

        val date = guessDateFromMessage(parsedMessage)
        val headers = parsedMessage.header.fields.map { fieldToHeader(it) }
        val references = parseReferences(parsedMessage.header.getFields("References"))

        val textParts: MutableList<String> = mutableListOf()
        val htmlParts: MutableList<String> = mutableListOf()

        val hints: MutableList<MessageHint> = mutableListOf()
        if (guessIsAutoMessage(parsedMessage)) {
            hints.add(MessageHint.IS_AUTO)

            if (guessIsOooSubject(subject)) {
                hints.add(MessageHint.IS_OOO)
            }
        }

        if (guessIsReply(parsedMessage)) {
            hints.add(MessageHint.IS_REPLY)
        }

        if (guessIsForward(parsedMessage)) {
            hints.add(MessageHint.IS_FORWARD)
        }

        walkMessageParts(parsedMessage) { part ->
            // - we don't care about processing multi-parts which are essentially containers
            // for the real parts we want to read. So we have this condition to ignore them
            // and only process actual parts.
            if (!part.isMultipart) {
                val disposition = part.dispositionType
                val filename = part.filename
                val mimeType = part.mimeType
                val contentId = part.parent?.header?.getField("Content-Id")?.body?.toString()
                val body = part.body

                // A body part is any text part that has no disposition
                // And we also handle the edge-case where the disposition is inline, but it's not a
                // a file (no filename, no content-id) which is something some clients do sometimes
                // when they feel like it
                if (body is TextBody && (
                            disposition == null
                                    || (disposition == "inline" && filename == null && contentId == null)
                            )
                ) {
                    if (mimeType == "text/html") {
                        htmlParts.add(IOUtils.toString(body.reader))
                    } else {
                        textParts.add(IOUtils.toString(body.reader))
                    }
                }
            }
        }

        val bodyHtml = if (htmlParts.isNotEmpty()) htmlParts.joinToString("").replace("\r\n", "\n")
            .replace("\r", "\n") else null
        val bodyText =
            if (textParts.isNotEmpty()) textParts.joinToString("\n").replace("\r\n", "\n")
                .replace("\r", "\n") else null

        return ParsedMessage(
            subject,
            messageId,
            fromAddress,
            senderAddress,
            replyToAddresses,
            returnPath,
            toAddresses,
            ccAddresses,
            date,
            references,
            bodyHtml,
            bodyText,
            headers,
            hints.toList(),
            sizeInputStream.bytesRead
        )
    }

    fun parseToResult(inputStream: InputStream, messageBuilder: MessageBuilder): ParseResult {
        val contextId = UUID.randomUUID().toString()
        var exception: Exception? = null

        val message = try {
            parse(inputStream, messageBuilder)
        } catch (e: Exception) {
            exception = e
            null
        }

        return ParseResult(message, exception, contextId)
    }

    fun parseToResult(inputStream: InputStream): ParseResult =
        parseToResult(inputStream, createDefaultMessageBuilder())

}