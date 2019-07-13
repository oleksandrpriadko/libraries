package com.android.oleksandrpriadko.mvp.repo_extension

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

abstract class RetrofitRepoExtension(private val baseUrl: String) : RepoExtension {
    private var mRetrofit: Retrofit? = null

    protected abstract val interceptors: Array<Interceptor>

    protected abstract val loggingLevel: HttpLoggingInterceptor.Level

    protected abstract val converterFactory: Converter.Factory

    private val retrofit: Retrofit?
        get() {
            if (this.mRetrofit == null) {
                this.initRetrofit()
            }
            return this.mRetrofit
        }

    init {
        this.initRetrofit()
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
        this.mRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
    }

    fun <A> getApi(apiClass: Class<A>): A {
        return this.retrofit!!.create(apiClass)
    }

    override fun cleanUp() {
        mRetrofit = null
    }

    interface Listener {

        fun onLoadingStarted()

        fun onLoadingDone()

        fun onLoadingError(throwable: Throwable)

    }
}