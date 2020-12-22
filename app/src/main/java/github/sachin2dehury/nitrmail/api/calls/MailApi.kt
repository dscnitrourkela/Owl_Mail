package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.mail.Mails
import github.sachin2dehury.nitrmail.others.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MailApi {

    @GET("{request}")
    suspend fun getMails(@Path("request") request: String = Constants.JUNK_URL): Response<Mails>
}