package github.sachin2dehury.nitrmail.api.data.tasks

data class Comp(
    val allDay: Boolean,
    val calItemId: String,
    val ciFolder: String,
    val `class`: String,
    val compNum: Int,
    val d: Long,
    val e: List<E>,
    val isOrg: Boolean,
    val loc: String,
    val method: String,
    val name: String,
    val noBlob: Boolean,
    val or: Or,
    val percentComplete: String,
    val priority: String,
    val rsvp: Boolean,
    val s: List<S>,
    val seq: Int,
    val status: String,
    val uid: String,
    val url: String,
    val x_uid: String
)