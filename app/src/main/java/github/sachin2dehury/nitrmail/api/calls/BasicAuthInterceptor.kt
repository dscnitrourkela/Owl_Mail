package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = Constants.NO_CREDENTIAL
    var token = Constants.NO_TOKEN
    var isApiCall = true

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val authenticatedRequest = if (token == Constants.NO_TOKEN) {
            request.newBuilder()
                .header("Authorization", credential)
                .build()
        } else {
            request.newBuilder()
                .addHeader("Cookie", token)
                .build()
        }

        val response = chain.proceed(authenticatedRequest)

        if (token == Constants.NO_TOKEN) {
            token = response.headers("Set-Cookie").first().substringBefore(';')
        }

        return response
    }
}