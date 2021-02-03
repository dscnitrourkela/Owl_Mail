package github.sachin2dehury.owlmail.api.calls

import github.sachin2dehury.owlmail.others.Constants
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var credential = Constants.NO_CREDENTIAL
    var token = Constants.NO_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {

        var response = request(chain)

        if (response.code == 401) {
            response.close()
            token = Constants.NO_TOKEN
            response = request(chain)
        }

        if (token == Constants.NO_TOKEN && response.headers("Set-Cookie").isNotEmpty()) {
            token = response.headers("Set-Cookie").first().substringBefore(';')
        }

        return response
    }

    private fun request(chain: Interceptor.Chain) = chain.proceed(
        when (token) {
            Constants.NO_TOKEN -> chain.request().newBuilder().header("Authorization", credential)
                .build()
            else -> chain.request().newBuilder().header("Cookie", token).build()
        }
    )
}