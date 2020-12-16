package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.entities.Mails
import github.sachin2dehury.nitrmail.api.data.remote.AccountRequest
import github.sachin2dehury.nitrmail.api.data.remote.SimpleResponse
import github.sachin2dehury.nitrmail.others.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MailApi {

    companion object {
        var request = Constants.JUNK_URL
//        var item = ""
    }

    @POST(Constants.JUNK_URL)
    suspend fun login(
        @Body loginRequest: AccountRequest
    ): Response<SimpleResponse>

    @GET("{request}")
    suspend fun getMails(@Path("request") request: String = MailApi.request): Response<Mails>

//    @GET("{item}")
//    suspend fun getMails(@Path("item") item: String = MailApi.item): Response<Mails>
}