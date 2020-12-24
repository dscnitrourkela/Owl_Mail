package github.sachin2dehury.nitrmail.api.data.calendar

data class Alarm(
    val action: String,
    val desc: List<Desc>,
    val trigger: List<Trigger>
)