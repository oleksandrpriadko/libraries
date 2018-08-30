package com.android.oleksandrpriadko.mvp.repo_extension;

import android.support.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;

public abstract class RetrofitRepoExtension implements RepoExtension {

    private final String mBaseUrl;
    private Retrofit mRetrofit;

    public RetrofitRepoExtension(@NonNull final String baseUrl) {
        this.mBaseUrl = baseUrl;
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
        this.mRetrofit = new Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(client)
            .addConverterFactory(getConverterFactory())
            .build();
    }

    private String getBaseUrl() {
        return this.mBaseUrl;
    }

    private Retrofit getRetrofit() {
        if (this.mRetrofit == null) {
            this.initRetrofit();
        }
        return this.mRetrofit;
    }

    public <A> A getApi(final Class<A> apiClass) {
        return this.getRetrofit().create(apiClass);
    }

    @Override
    public void cleanUp() {
        mRetrofit = null;
    }

    public interface Listener {

        void onLoadingStarted();

        void onLoadingDone();

        void onLoadingError(@NonNull Throwable throwable);

    }
}