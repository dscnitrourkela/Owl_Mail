package github.sachin2dehury.nitrmail.api.calls

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import github.sachin2dehury.nitrmail.api.data.Mails
import github.sachin2dehury.nitrmail.api.data.ParsedMail
import github.sachin2dehury.nitrmail.others.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber
import java.io.IOException

class AppClient {

    private val _mails = MutableLiveData<Mails>()
    val mails: LiveData<Mails> = _mails

    private val _item = MutableLiveData<ParsedMail>()
    val item: LiveData<ParsedMail> = _item

    private val _credential = MutableLiveData<String>()
    val credential: LiveData<String> = _credential

    private val client = OkHttpClient.Builder().authenticator { _, response ->
        response.request.newBuilder().header("Authorization", credential.value!!).build()
    }.build()

    fun setCredentials(credential: String) {
        _credential.postValue(credential)
    }

    fun makeMailRequest(url: String) = CoroutineScope(Dispatchers.IO).launch {
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

    fun makeItemRequest(item: String) = CoroutineScope(Dispatchers.IO).launch {
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
            val data = it.substringAfter(Constants.MIME_TAG)
            val sender = data.substringAfter(Constants.FROM_TAG).substringBefore(Constants.TO_TAG)
            val date = data.substringAfter(Constants.DATE_TAG).substringBefore(Constants.UTC_TAG)
            val subject =
                data.substringAfter(Constants.SUBJECT_TAG)
                    .substringBefore(Constants.CONTENT_TYPE_TAG)
            val contentType = data.substringAfter(Constants.CONTENT_TYPE_TAG)
                .substringBefore(Constants.CHAR_SET_TAG)
            val charSet =
                data.substringAfter(Constants.CHAR_SET_TAG).substringBefore(Constants.ENCODING_TAG)
            val encoding =
                data.substringAfter(Constants.ENCODING_TAG)
                    .substringBefore(Constants.MESSAGE_ID_TAG)
            val messageID =
                data.substringAfter(Constants.MESSAGE_ID_TAG).substringBefore(Constants.CLOSE_TAG)
            var messageBody = data.substringAfter(messageID + Constants.CLOSE_TAG)
            if (encoding.contains(Constants.BASE_64)) {
                messageBody = Base64.decode(messageBody, Base64.DEFAULT).decodeToString()
            }
            val result =
                ParsedMail(
                    sender,
                    date,
                    subject,
                    contentType,
                    charSet,
                    encoding,
                    messageID,
                    messageBody
                )
            _item.postValue(result)
//            Log.d("Test", result.toString())
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