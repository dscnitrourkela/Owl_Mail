package github.sachin2dehury.nitrmail.api.data.calander

data class Appt(
    val d: Long,
    val f: String,
    val id: String,
    val inv: List<Inv>,
    val l: String,
    val rev: Int,
    val s: Int,
    val uid: String
)