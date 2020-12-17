package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.entities.Mails
import github.sachin2dehury.nitrmail.others.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MailApi {

    companion object {
        const val request = Constants.JUNK_URL
    }

    @GET("{request}")
    suspend fun getMails(@Path("request") request: String = MailApi.request): Response<Mails>
}