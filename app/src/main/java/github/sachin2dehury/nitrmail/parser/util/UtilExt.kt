package github.sachin2dehury.nitrmail.parser.util

import github.sachin2dehury.nitrmail.parser.data.*
import org.apache.james.mime4j.dom.Entity
import org.apache.james.mime4j.dom.Message
import org.apache.james.mime4j.dom.Multipart
import org.apache.james.mime4j.dom.SingleBody
import org.apache.james.mime4j.dom.address.AddressList
import org.apache.james.mime4j.dom.address.Mailbox
import org.apache.james.mime4j.dom.address.MailboxList
import org.apache.james.mime4j.dom.field.*
import org.apache.james.mime4j.field.datetime.parser.DateTimeParser
import org.apache.james.mime4j.stream.Field
import java.io.StringReader
import java.util.*

fun mailboxToAddress(mb: Mailbox): Address {
    val cleanName = mb.name?.trim()?.trim('"', '\'') ?: ""
    val name = if (cleanName.isNotEmpty()) cleanName else null

    return Address(name, "${mb.localPart}@${mb.domain}")
}

fun Mailbox.toAddress(): Address = mailboxToAddress(this)

fun mailboxListToAddressList(mbs: MailboxList): List<Address> {
    return mbs.map { mailboxToAddress(it) }
}

fun MailboxList.toAddressList(): List<Address> = mailboxListToAddressList(this)

fun mailboxListToAddressList(mbs: AddressList): List<Address> {
    return mbs.flatten().map { mailboxToAddress(it) }
}

fun AddressList.toAddressList(): List<Address> = mailboxListToAddressList(this)


fun fieldToHeader(field: Field): HeaderInterface {
    val name = field.name ?: ""
    val value = field.body ?: ""

    if (field is ParsedField && !field.isValidField) {
        val errMsg = field.parseException.message ?: "Unknown"
        return ParseErrorHeader(name, value, errMsg)
    }

    // DateTimeFieldLenientImpl doesnt raise any errors,
    // it just returns a null date
    if (field is DateTimeField && field.date == null) {
        val errMsg = field.parseException.message ?: "Invalid date format"
        return ParseErrorHeader(name, value, errMsg)
    }

    return when (field) {
        is DateTimeField -> DateHeader(name, value, field.date)
        is AddressListField -> MailboxListHeader(
            name,
            value,
            mailboxListToAddressList(field.addressList)
        )
        is MailboxListField -> MailboxListHeader(
            name,
            value,
            mailboxListToAddressList(field.mailboxList)
        )
        is MailboxField -> MailboxHeader(name, value, mailboxToAddress(field.mailbox))
        else -> Header(name, value)
    }
}

fun parseReferences(fields: List<Field>): List<String> {
    return fields.flatMap { field ->
        field.body?.split("\\s+".toRegex())?.map { it.trim() } ?: listOf()
    }
}

fun guessDateFromMessage(msg: Message): Date? {
    if (msg.date != null) {
        return msg.date
    }

    val rec = msg.header.getField("Received")?.body
    if (rec == null) {
        return null
    }

    val parts = rec.split(';', limit = 2)
    if (parts.size != 2) {
        return null
    }

    val dateStr = parts[1]
    return try {
        DateTimeParser(StringReader(dateStr)).parseAll().date
    } catch (e: Exception) {
        null
    }
}

fun getReturnPathAddr(field: Field): Address? {
    if (field is MailboxField) {
        return if (field.mailbox != null) {
            mailboxToAddress(field.mailbox)
        } else {
            null
        }
    }

    val body = field.body
    if (body == null) {
    }

    if (body == "<>") {
        // special no return value should be kept
        return Address("NORETURN", "<>")
    }

    return null
}

fun walkMessageParts(message: Entity, block: (b: Entity) -> Unit) {
    when (val body = message.body) {
        is Multipart -> {
            block(message)
            for (sub in body.bodyParts) {
                walkMessageParts(sub, block)
            }
        }

        is SingleBody -> {
            block(message)
        }

        is Message -> {
            block(message)
            walkMessageParts(message, block)
        }
    }
}

fun guessIsAutoMessage(msg: Message): Boolean {
    if (msg.header.hasHeaderEqual("Preference", "auto_reply")) {
        return true
    }

    if (
        msg.header.hasHeaderEqual("Auto-Submitted", "auto-replied")
        || msg.header.hasHeaderEqual("X-Autoreply", "yes")
    ) {
        return true
    }

    if (msg.header.hasHeaderEqual("X-POST-MessageClass", "9; Autoresponder")) {
        return true
    }

    if (
        msg.header.hasHeader("X-Autorespond")
        || msg.header.hasHeader("X-AutoReply-From")
        || msg.header.hasHeader("X-Mail-Autoreply")
        || msg.header.hasHeader("X-FC-MachineGenerated")
    ) {
        return true
    }

    if (msg.header.hasHeaderEqual("Delivered-To", "Autoresponder")) {
        return true;
    }

    if (msg.header.hasHeader("Auto-Submitted") && !msg.header.hasHeaderEqual(
            "Auto-Submitted",
            "no"
        )
    ) {
        return true
    }

    if (msg.header.hasHeader("X-Cron-Env")) {
        return true
    }

    if (msg.header.hasHeaderEqual("X-Auto-Response-Suppress", "OOF")) {
        return true
    }

    listOf("junk", "bulk", "list", "auto_reply").forEach {
        if (msg.header.hasHeaderEqual("Precedence", it) || msg.header.hasHeaderEqual(
                "X-Precedence",
                it
            )
        ) {
            return true
        }
    }

    return false
}


fun guessIsOooSubject(subject: String): Boolean {
    val s = subject.toLowerCase()
    return s.startsWith("out of office:")
            || s.startsWith("out of the office:")
            || s.startsWith("out of office autoreply:")
            || s.startsWith("out of office reply:")
            || s.startsWith("automatic reply:")
            || s.endsWith("is out of the office")
}

fun guessIsReply(msg: Message): Boolean {
    if (msg.header.getFields("References").isNotEmpty()) {
        return true
    }

    val s = msg.subject.toLowerCase()
    return s.matches("^re:".toRegex())
}

fun guessIsForward(msg: Message): Boolean {
    val s = msg.subject.toLowerCase()
    return s.matches("^(fwd|fw):".toRegex())
}

fun org.apache.james.mime4j.dom.Header.hasHeader(name: String): Boolean =
    this.getField(name) != null

fun org.apache.james.mime4j.dom.Header.hasHeaderEqual(
    name: String,
    value: String,
    case: Boolean = true
): Boolean {
    return if (case) {
        val lowerValue = value.toLowerCase()
        this.getFields(name).any { it.body != null && it.body.toLowerCase() == lowerValue }
    } else {
        this.getFields(name).any { it.body == value }
    }
}