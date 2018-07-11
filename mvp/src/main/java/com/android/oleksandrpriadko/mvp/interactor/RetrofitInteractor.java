package com.android.oleksandrpriadko.mvp.interactor;

import android.support.annotation.NonNull;

import com.google.common.cache.Cache;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;

public abstract class RetrofitInteractor {

    private final String baseUrl;
    private Retrofit retrofit;
    private final Cache mCache;

    public RetrofitInteractor(String baseUrl) {
        this(baseUrl, null);
    }

    public RetrofitInteractor(String baseUrl, Cache cache) {
        this.baseUrl = baseUrl;
        this.mCache = cache;
        this.initRetrofit();
    }

    protected abstract Interceptor[] getInterceptors();

    @NonNull
    protected abstract HttpLoggingInterceptor.Level getLoggingLevel();

    protected abstract Converter.Factory getConverterFactory();

    private void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(getLoggingLevel());
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(interceptor);
        Interceptor[] interceptors = getInterceptors();
        for (int i = 0; i < interceptors.length; ++i) {
            builder.addInterceptor(getInterceptors()[i]);
        }

        OkHttpClient client = builder.build();
        String testUrl = getBaseUrl();
        this.retrofit = new Retrofit.Builder()
            .baseUrl(testUrl)
            .client(client)
            .addConverterFactory(getConverterFactory())
            .build();
    }

    private String getBaseUrl() {
        return this.baseUrl;
    }

    private Retrofit getRetrofit() {
        if (this.retrofit == null) {
            this.initRetrofit();
        }
        return this.retrofit;
    }

    protected <A> A getApi(final Class<A> apiClass) {
        return this.getRetrofit().create(apiClass);
    }

    public interface ProcessListener {

        void onLoadingStarted();

        void onLoadingDone();

        void onLoadingError(@NonNull Throwable throwable);

    }
}
