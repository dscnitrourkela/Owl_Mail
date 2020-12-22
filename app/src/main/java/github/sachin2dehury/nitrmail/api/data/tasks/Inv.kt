package github.sachin2dehury.nitrmail.api.data.tasks

data class Inv(
    val comp: List<Comp>,
    val compNum: Int,
    val id: Int,
    val seq: Int,
    val type: String
)