package github.sachin2dehury.nitrmail.parser.data

data class Address(
    val name: String? = "",
    val email: String
) {
    override fun toString(): String {
        return if (name != null) {
            "$name <$email>"
        } else {
            email
        }
    }
}

