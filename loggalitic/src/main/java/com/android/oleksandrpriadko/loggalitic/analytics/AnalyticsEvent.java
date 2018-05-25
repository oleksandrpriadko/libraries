package com.android.oleksandrpriadko.loggalitic.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Oleksandr Priadko.
 * 7/9/17
 * <p>
 * Generic event which will be converted to necessary type.
 */
public class AnalyticsEvent {

    private static final String DESCRIPTION = "description";

    private String mName;
    private final Bundle mAttributes = new Bundle();

    public AnalyticsEvent(@NonNull String name) {
        checkArgument(!TextUtils.isEmpty(name));
        this.mName = name;
    }

    public AnalyticsEvent(String name, String description) {
        this(name);
        this.mAttributes.putString(DESCRIPTION, description);
    }


    public String getName() {
        return mName;
    }

    public Bundle getAttributes() {
        return mAttributes;
    }
}
