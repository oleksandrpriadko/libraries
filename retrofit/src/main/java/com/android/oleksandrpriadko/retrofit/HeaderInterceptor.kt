package com.android.oleksandrpriadko.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

abstract class HeaderInterceptor : Interceptor {

    protected abstract val headers: Array<Header>

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val builder = request.newBuilder().addHeader(TYPE_TITLE, TYPE_VALUE)
        val headers = headers
        for (header in headers) {
            builder.addHeader(header.name, header.value)
        }
        request = builder.build()
        return chain.proceed(request)
    }

    class Header(val name: String, val value: String)

    companion object {
        private const val TYPE_TITLE = "Accept"
        private const val TYPE_VALUE = "application/json"
    }
}
