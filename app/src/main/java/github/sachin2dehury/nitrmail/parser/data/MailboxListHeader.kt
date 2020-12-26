package github.sachin2dehury.nitrmail.parser.data

data class MailboxListHeader(
    override val name: String = "",
    override val value: String = "",
    val addresses: List<Address> = emptyList()
) : HeaderInterface