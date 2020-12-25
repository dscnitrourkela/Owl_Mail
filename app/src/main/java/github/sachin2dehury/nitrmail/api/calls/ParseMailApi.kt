package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.parsedmails.ParsedMail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface ParseMailApi {

    @GET
    suspend fun getParsedMail(
        @Body request: String
    ): Response<ParsedMail>
}