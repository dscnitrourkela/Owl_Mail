package github.sachin2dehury.nitrmail.others

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {

    companion object {
        fun <T> success(data: T?) = Resource(Status.SUCCESS, data, null)

        fun <T> loading(data: T?) = Resource(Status.LOADING, data, null)

        fun <T> error(data: T?, message: String?) = Resource(Status.ERROR, data, message)
    }
}
