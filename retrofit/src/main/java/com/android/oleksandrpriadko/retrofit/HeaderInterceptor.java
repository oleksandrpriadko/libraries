package com.android.oleksandrpriadko.retrofit;

import java.io.IOException;

import android.support.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HeaderInterceptor implements Interceptor {
    private static final String TYPE_TITLE = "Accept";
    private static final String TYPE_VALUE = "application/json";

    protected abstract @NonNull SimplePairStrings[] getHeaders();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder().addHeader(TYPE_TITLE, TYPE_VALUE);
        SimplePairStrings[] headers = getHeaders();
        for (SimplePairStrings header : headers) {
            builder.addHeader(header.first, header.second);
        }
        request = builder.build();
        return chain.proceed(request);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //  SIMPLE PAIR
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class SimplePairStrings{
        private String first;
        private String second;

        public SimplePairStrings(String first, String second) {
            this.first = first;
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }
    }
}
