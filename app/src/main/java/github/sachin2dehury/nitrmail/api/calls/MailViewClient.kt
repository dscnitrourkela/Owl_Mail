package github.sachin2dehury.nitrmail.api.calls

import android.webkit.WebViewClient
import github.sachin2dehury.nitrmail.others.Constants

class MailViewClient : WebViewClient() {

    var token = Constants.NO_TOKEN

//    override fun shouldInterceptRequest(
//        view: WebView?,
//        request: WebResourceRequest?
//    ): WebResourceResponse? {
//        request?.requestHeaders?.put("Cookie", token)
//        debugLog("$request")
//        return super.shouldInterceptRequest(view, request)
//    }
}