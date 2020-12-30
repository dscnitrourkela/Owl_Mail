package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.Mails
import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MailApi {

    @GET(Constants.HOME_URL + Constants.DRAFT_URL)
    suspend fun login(
        @Query("query") sync: String,
        @Query("auth") token: String = Constants.AUTH_TOKEN
    ): Response<Mails>

    @GET("${Constants.HOME_URL}{request}")
    suspend fun getMails(
        @Path("request") request: String,
        @Query("query") search: String,
    ): Response<Mails>

    @GET(Constants.HOME_URL)
    suspend fun getMailItem(
        @Query("id") mailId: String,
        @Query("part") part: String = "1"
    ): ResponseBody

    @GET(Constants.MESSAGE_URL)
    suspend fun getMailItemHtml(
        @Query("id") mailId: String,
        @Query("auth") token: String = Constants.AUTH_TOKEN
    ): ResponseBody


}