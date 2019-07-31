package com.android.oleksandrpriadko.mvp.repo_extension

import com.android.oleksandrpriadko.core.CoreServiceManager
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

    /**
     * return null if there is no internet connection
     */
    fun <T> getApi(apiClass: Class<T>): T? {
        return if (CoreServiceManager.isOnline()) {
            retrofit!!.create(apiClass)
        } else {
            null
        }
    }

    override fun cleanUp() {
        retrofit = null
    }

    companion object {
        const val RESPONSE_NOT_SUCCESSFUL = "Response is not successful"
        const val RESPONSE_NULL = "Response content is null"
        const val RESPONSE_EMPTY = "Response content is empty"
    }

    interface Listener {

        fun onLoadingStarted()

        fun onLoadingDone()

        fun onLoadingError()

        fun onNoInternet()

    }
}