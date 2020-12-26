package github.sachin2dehury.nitrmail.parser.util

import github.sachin2dehury.nitrmail.parser.data.Address
import github.sachin2dehury.nitrmail.parser.data.MessageHint
import github.sachin2dehury.nitrmail.parser.data.ParsedMail
import org.apache.commons.io.IOUtils
import org.apache.james.mime4j.dom.Message
import org.apache.james.mime4j.dom.MessageBuilder
import org.apache.james.mime4j.dom.TextBody
import org.apache.james.mime4j.field.LenientFieldParser
import org.apache.james.mime4j.field.MailboxFieldLenientImpl
import org.apache.james.mime4j.message.DefaultMessageBuilder
import java.io.InputStream

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

    fun parse(
        inputStream: InputStream,
        messageBuilder: MessageBuilder = createDefaultMessageBuilder()
    ): ParsedMail {
        val sizeInputStream = SizeInputStream(inputStream)
        var mail = ParsedMail()
        messageBuilder.parseMessage(sizeInputStream)?.let {
            mail = parseHelper(it)
        }
        return mail
    }

    private fun parseHelper(parsedMessage: Message): ParsedMail {

        val messageId = parsedMessage.messageId ?: ""

        val subject = parsedMessage.subject ?: ""
        val fromAddress: Address = parsedMessage.from?.toAddressList()?.first() ?: Address()
        val senderAddress: Address = parsedMessage.sender?.toAddress() ?: Address()
        val replyToAddresses: List<Address> = parsedMessage.replyTo?.toAddressList() ?: emptyList()
        val toAddresses: List<Address> = parsedMessage.to?.toAddressList() ?: emptyList()
        val ccAddresses: List<Address> = parsedMessage.cc?.toAddressList() ?: emptyList()

        val returnPathField = parsedMessage.header?.getField("Return-Path")
        val returnPath = returnPathField?.let { getReturnPathAddress(it) } ?: Address()

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
                if (body is TextBody && (disposition == null ||
                            (disposition == "inline" && filename == null && contentId == null))
                ) {
                    if (mimeType == "text/html") {
                        htmlParts.add(IOUtils.toString(body.reader))
                    } else {
                        textParts.add(IOUtils.toString(body.reader))
                    }
                }
            }
        }

        val bodyHtml = htmlParts.joinToString("").replace("\r\n", "\n")
            .replace("\r", "\n")
        val bodyText = textParts.joinToString("\n").replace("\r\n", "\n")
            .replace("\r", "\n")

        return ParsedMail(
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
            hints.toList()
        )
    }
//
//    private fun parseToResult(
//        inputStream: InputStream,
//        messageBuilder: MessageBuilder
//    ): ParseResult {
//        val contextId = UUID.randomUUID().toString()
//        var exception: Exception? = null
//
//        val message = try {
//            parse(inputStream, messageBuilder)
//        } catch (e: Exception) {
//            exception = e
//            null
//        }
//
//        return ParseResult(message, exception, contextId)
//    }
//
//    fun parseToResult(inputStream: InputStream): ParseResult =
//        parseToResult(inputStream, createDefaultMessageBuilder())

}