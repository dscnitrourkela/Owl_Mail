package github.sachin2dehury.nitrmail.api.data.tasks

data class Task(
    val d: Long,
    val id: String,
    val inv: List<Inv>,
    val l: String,
    val rev: Int,
    val s: Int,
    val uid: String
)