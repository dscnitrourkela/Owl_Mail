package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.mails.Mails
import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MailApi {

    @GET("${Constants.HOME_URL}{request}")
    suspend fun getMails(
        @Path("request") request: String,
        @Query("query") sync: String,
    ): Response<Mails>

    @GET(Constants.HOME_URL)
    suspend fun getMailItem(
        @Query("id") mailId: String
    ): ResponseBody
}