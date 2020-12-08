package github.sachin2dehury.nitrmail.api.calls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import github.sachin2dehury.nitrmail.api.data.Mails
import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.*
import timber.log.Timber
import java.io.IOException

class AppClient {

    private val _mails = MutableLiveData<Mails>()
    val mails: LiveData<Mails> = _mails

    private val _item = MutableLiveData<String>()
    val item: LiveData<String> = _item

    private val _credential = MutableLiveData<String>()
    val credential: LiveData<String> = _credential

    private val client = OkHttpClient.Builder().authenticator { _, response ->
        response.request.newBuilder().header("Authorization", credential.value!!).build()
    }.build()

    fun setCredentials(credential: String) {
        _credential.postValue(credential)
    }

    fun makeMailRequest(url: String): LiveData<Mails> {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.w(this::javaClass.name, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                parseMails(response)
            }
        })
        return mails
    }

    fun makeItemRequest(item: String) {
        val url = Constants.ITEM_URL + item
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.w(this::javaClass.name, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                fetchItem(response)
            }
        })
    }

    private fun fetchItem(response: Response) {
        val responseBody = response.body?.string()
        responseBody?.let {
            val result = it.substringAfter("Mime Version : 1.0")
            _item.postValue(result)
        }
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