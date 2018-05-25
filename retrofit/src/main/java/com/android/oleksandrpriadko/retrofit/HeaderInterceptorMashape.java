package com.android.oleksandrpriadko.retrofit;

import android.support.annotation.NonNull;

public class HeaderInterceptorMashape extends HeaderInterceptor {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MASHAPE_TITLE
        = "X-Mashape-Key";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MASHAPE_VALUE
        = "ZqZDokvSBlmshEMoSxzqhog0kOcOp1tnJgvjsnGOhZhcrGnywl";

    @NonNull
    @Override
    protected Header[] getHeaders() {
        Header header = new Header(MASHAPE_TITLE, MASHAPE_VALUE);
        return new Header[]{header};
    }
}
