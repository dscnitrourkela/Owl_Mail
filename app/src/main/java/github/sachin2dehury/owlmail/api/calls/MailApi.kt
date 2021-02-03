package github.sachin2dehury.owlmail.api.calls

import github.sachin2dehury.owlmail.api.data.Mails
import github.sachin2dehury.owlmail.others.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MailApi {

    @GET(Constants.HOME_URL + Constants.TRASH_URL + Constants.AUTH_SET_COOKIE)
    suspend fun login(
        @Query("query") sync: String,
    ): Response<Mails>

    @GET(Constants.HOME_URL + "{request}" + Constants.AUTH_FROM_COOKIE)
    suspend fun getMails(
        @Path("request") request: String,
        @Query("query") search: String,
    ): Response<Mails>

    @GET(Constants.MOBILE_URL + Constants.AUTH_FROM_COOKIE + Constants.CLIENT_VIEW + Constants.LOAD_IMAGES)
    suspend fun getParsedMail(
        @Query("id") id: String,
    ): ResponseBody

    @GET(Constants.HTML_URL + Constants.AUTH_FROM_COOKIE + Constants.LOAD_IMAGES)
    suspend fun getMailBody(
        @Query("id") id: String,
    ): ResponseBody
}