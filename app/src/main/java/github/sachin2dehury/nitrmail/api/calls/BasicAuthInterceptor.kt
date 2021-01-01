package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = Constants.NO_CREDENTIAL
    var token = Constants.NO_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {

        var response = makeRequest(chain)

        if (response.code == 401) {
            token = Constants.NO_TOKEN
            response = makeRequest(chain)
        }

        try {
            getToken(response)
        } catch (e: Exception) {
            token = Constants.NO_TOKEN
            response = makeRequest(chain)
            getToken(response)
        }

        return response
    }

    private fun getToken(response: Response) {
        if (token == Constants.NO_TOKEN) {
            token = response.headers("Set-Cookie").first().substringBefore(';')
        }
    }

    private fun makeRequest(chain: Interceptor.Chain): Response {

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

        return chain.proceed(authenticatedRequest)
    }
}