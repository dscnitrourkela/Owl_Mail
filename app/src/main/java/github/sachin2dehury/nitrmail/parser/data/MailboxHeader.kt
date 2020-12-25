package github.sachin2dehury.nitrmail.parser.data

data class MailboxHeader(
    override val name: String,
    override val value: String,
    val address: Address
) : HeaderInterface
