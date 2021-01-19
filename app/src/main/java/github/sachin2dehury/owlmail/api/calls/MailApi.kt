package github.sachin2dehury.owlmail.api.calls

import github.sachin2dehury.owlmail.api.data.Mails
import github.sachin2dehury.owlmail.others.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MailApi {

    @GET(Constants.HOME_URL + Constants.DRAFT_URL)
    suspend fun login(
        @Query("query") sync: String,
        @Query(Constants.AUTH) token: String = Constants.AUTH_SET_COOKIE
    ): Response<Mails>

    @GET("${Constants.HOME_URL}{request}")
    suspend fun getMails(
        @Path("request") request: String,
        @Query("query") search: String,
        @Query(Constants.AUTH) token: String = Constants.AUTH_COOKIE
    ): Response<Mails>

    @GET("{request}")
    suspend fun getMailItemBody(
        @Path("request") request: String,
        @Query("id") mailId: String,
        @Query("xim") loadImage: String = "1",
        @Query(Constants.AUTH) token: String = Constants.AUTH_COOKIE,
    ): ResponseBody

}