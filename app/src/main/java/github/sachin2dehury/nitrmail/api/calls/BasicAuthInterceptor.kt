package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.debugLog
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = Constants.NO_CREDENTIAL
    var token = Constants.NO_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {

        var response = makeRequest(chain)

        if (response.code == 401) {
            response.close()
            token = Constants.NO_TOKEN
            response = makeRequest(chain)
        }

        if (token == Constants.NO_TOKEN && response.headers("Set-Cookie").isNotEmpty()) {
            token = response.headers("Set-Cookie").first().substringBefore(';')
        }

        debugLog(token.length.toString())

        return response
    }

    private fun makeRequest(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val authenticatedRequest = if (token == Constants.NO_TOKEN) {
            request.newBuilder()
                .header("Authorization", credential)
                .build()
        } else {
            request.newBuilder()
                .header("Cookie", token)
                .build()
        }

        return chain.proceed(authenticatedRequest)
    }
}