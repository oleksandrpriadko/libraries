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
    protected SimplePairStrings[] getHeaders() {
        SimplePairStrings simplePairStrings = new SimplePairStrings(MASHAPE_TITLE, MASHAPE_VALUE);
        return new SimplePairStrings[]{simplePairStrings};
    }
}
