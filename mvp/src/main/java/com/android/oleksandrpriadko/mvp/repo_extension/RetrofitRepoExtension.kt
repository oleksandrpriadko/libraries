package com.android.oleksandrpriadko.mvp.repo_extension

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException

class RetrofitRepoExtension(
        private val baseUrl: String,
        private val interceptors: List<Interceptor> = listOf(),
        private val loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
        private val converterFactory: Converter.Factory) : RepoExtension {

    private var retrofit: Retrofit? = null
        get() {
            if (field == null) {
                initRetrofit()
            }
            return field
        }

    init {
        initRetrofit()
    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = loggingLevel
        val builder = OkHttpClient.Builder().addInterceptor(interceptor)
        val interceptors = interceptors
        for (i in interceptors.indices) {
            builder.addInterceptor(interceptors[i])
        }

        val client = builder.build()
        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
    }

    fun <T> getApi(apiClass: Class<T>): T {
        return retrofit!!.create(apiClass)
    }

    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return false
    }

    override fun cleanUp() {
        retrofit = null
    }

    interface Listener {

        fun onLoadingStarted()

        fun onLoadingDone()

        fun onLoadingError(throwable: Throwable)

        fun onNoInternet()

    }
}