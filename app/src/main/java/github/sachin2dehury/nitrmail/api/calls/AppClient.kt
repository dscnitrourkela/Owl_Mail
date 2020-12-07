package github.sachin2dehury.nitrmail.api.calls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import github.sachin2dehury.nitrmail.api.data.Mails
import okhttp3.*
import timber.log.Timber
import java.io.IOException

class AppClient {

    private val credential = Credentials.basic("username", "password")

    private val client = OkHttpClient.Builder().authenticator { _, response ->
        response.request.newBuilder().header("Authorization", credential).build()
    }.build()

    private val _mails = MutableLiveData<Mails>()
    val mails: LiveData<Mails> = _mails

    fun makeRequest(url: String) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.w(this::javaClass.name, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                parseMails(response)
            }
        })
    }

    private fun parseMails(response: Response) {
        val jsonMailAdapter = Moshi.Builder().build()?.adapter(Mails::class.java)
        val responseBody = response.body?.string()
        responseBody?.let {
            val result = jsonMailAdapter?.fromJson(responseBody)
            _mails.postValue(result)
        }
    }
}