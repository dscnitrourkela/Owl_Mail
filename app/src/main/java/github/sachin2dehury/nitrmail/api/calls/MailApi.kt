package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.Mails
import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MailApi {

    @GET(Constants.HOME_URL + Constants.INBOX_URL)
    suspend fun login(
        @Query("query") sync: String,
        @Query("auth") token: String = Constants.AUTH_SET_COOKIE
    ): Response<Mails>

    @GET("${Constants.HOME_URL}{request}")
    suspend fun getMails(
        @Path("request") request: String,
        @Query("query") search: String,
        @Query("auth") token: String = Constants.AUTH_SET_COOKIE
    ): Response<Mails>

//    @GET(Constants.HOME_URL)
//    suspend fun getMailItem(
//        @Query("id") mailId: String,
//        @Query("part") part: String = "1",
//        @Query("auth") token: String = Constants.AUTH_COOKIE
//    ): ResponseBody

    @GET("{request}")
    suspend fun getMailItemBody(
        @Path("request") request: String,
        @Query("id") mailId: String,
        @Query("xim") loadImage: String,
        @Query("auth") token: String = Constants.AUTH_COOKIE,
    ): ResponseBody

}