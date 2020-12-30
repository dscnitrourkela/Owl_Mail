package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = Constants.NO_CREDENTIAL
    var token = Constants.NO_CREDENTIAL
    var isApiCall = true

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val authenticatedRequest = request.newBuilder()
            .header("Authorization", credential)
            .build()

        val response = chain.proceed(authenticatedRequest)

        token = response.headers("Set-Cookie").first().substringAfter('=').substringBefore(';')

        return response
    }
}