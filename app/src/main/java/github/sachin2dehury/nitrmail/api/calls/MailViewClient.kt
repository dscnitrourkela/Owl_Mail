package github.sachin2dehury.nitrmail.api.calls

import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.debugLog

class MailViewClient : WebViewClient() {

    var token = Constants.NO_TOKEN

    @Suppress("DEPRECATION")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val newUrl = url?.replace(
            "${Constants.AUTH}=${Constants.AUTH_COOKIE}",
            "${Constants.AUTH}=${Constants.AUTH_QUERY}&${Constants.AUTH_TOKEN_QUERY}=$token"
        )
        debugLog("shouldInterceptRequest : $newUrl")
        return super.shouldInterceptRequest(view, newUrl)
    }
}