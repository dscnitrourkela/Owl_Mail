package github.sachin2dehury.owlmail.utils

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
) = flow {
    val data = query().first()
    val flow = when {
        shouldFetch(data) -> {
            emit(data)
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)
            query().map { it }
        }
        else -> query().map { it }
    }
    emitAll(flow)
}