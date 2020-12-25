package github.sachin2dehury.nitrmail.api.calls

import github.sachin2dehury.nitrmail.api.data.parsedmails.ParsedMail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers

interface ParseMailApi {

    @Headers(
        "Content-Type : application/json",
        "Cookie : __cfduid=d15d999e9837b37812dae82b73be7a68c1608839791"
    )
    @GET("")
    suspend fun getParsedMail(
        @Body request: String
    ): Response<ParsedMail>
}