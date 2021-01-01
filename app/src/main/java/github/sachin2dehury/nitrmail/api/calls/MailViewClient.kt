package github.sachin2dehury.nitrmail.api.calls

import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import github.sachin2dehury.nitrmail.others.Constants
import okhttp3.OkHttpClient
import okhttp3.Request

class MailViewClient(
    private val okHttpClient: OkHttpClient
) : WebViewClient() {

    var token = Constants.NO_TOKEN

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val request = Request.Builder().url(url!!).addHeader("Cookie", token).build()
        val response = okHttpClient.newCall(request).execute()
        return WebResourceResponse(
            response.header("text/html", response.body?.contentType()?.type),
            response.header("content-encoding", "utf-8"),
            response.body?.byteStream()
        )
    }
}