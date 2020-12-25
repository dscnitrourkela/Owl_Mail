package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.parsedmails.ParsedMail
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ParseMailApi {

    @POST("/")
    suspend fun getParsedMail(
        @Body request: RequestBody
    ): Response<ParsedMail>
}