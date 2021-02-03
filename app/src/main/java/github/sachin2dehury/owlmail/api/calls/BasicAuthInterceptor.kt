package github.sachin2dehury.owlmail.api.calls

import github.sachin2dehury.owlmail.others.Constants
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = Constants.NO_CREDENTIAL
    var token = Constants.NO_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {

        var response = makeRequest(chain)

//        if (response.body?.string().toString().contains("must authenticate", true)) {
        if (response.code == 401) {
            response.close()
            token = Constants.NO_TOKEN
            response = makeRequest(chain)
        }

        if (token == Constants.NO_TOKEN && response.headers("Set-Cookie").isNotEmpty()) {
            token = response.headers("Set-Cookie").first().substringBefore(';')
        }

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