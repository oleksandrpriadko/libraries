package com.android.oleksandrpriadko.retrofit;

import java.io.IOException;

import android.support.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HeaderInterceptor implements Interceptor {
    private static final String TYPE_TITLE = "Accept";
    private static final String TYPE_VALUE = "application/json";

    @NonNull
    protected abstract Header[] getHeaders();

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder().addHeader(TYPE_TITLE, TYPE_VALUE);
        Header[] headers = getHeaders();
        for (Header header : headers) {
            builder.addHeader(header.getName(), header.getValue());
        }
        request = builder.build();
        return chain.proceed(request);
    }

    public static final class Header {

        private final String mName;
        private final String mValue;

        public Header(String name, String value) {
            mName = name;
            mValue = value;
        }

        public String getName() {
            return mName;
        }

        public String getValue() {
            return mValue;
        }
    }
}
