package com.android.oleksandrpriadko.mvp.repo_extension

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

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

    fun <A> getApi(apiClass: Class<A>): A {
        return retrofit!!.create(apiClass)
    }

    override fun cleanUp() {
        retrofit = null
    }

    interface Listener {

        fun onLoadingStarted()

        fun onLoadingDone()

        fun onLoadingError(throwable: Throwable)

    }
}