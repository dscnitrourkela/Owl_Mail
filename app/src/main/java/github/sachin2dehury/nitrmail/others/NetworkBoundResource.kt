package github.sachin2dehury.nitrmail.others

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline update: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    emit(Resource.loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))

        try {
//            val fetchedResult = if (data == null) fetch() else update()
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)
            query().map { Resource.success(it) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            query().map {
                Resource.error("Couldn't reach server. It might be down", it)
            }
        }
    } else {
        query().map { Resource.success(it) }
    }
    emitAll(flow)
}