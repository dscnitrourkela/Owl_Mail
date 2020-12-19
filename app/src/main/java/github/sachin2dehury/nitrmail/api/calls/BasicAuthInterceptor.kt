package github.sachin2dehury.nitrmail.api.calls

import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", credential)
            .build()
        return chain.proceed(authenticatedRequest)
    }
}