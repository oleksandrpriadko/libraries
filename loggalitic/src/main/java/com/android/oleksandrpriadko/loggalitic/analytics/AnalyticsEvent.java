package com.android.oleksandrpriadko.loggalitic.analytics;

import android.os.Bundle;

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

    public String getName() {
        return mName;
    }

    public Bundle getAttributes() {
        return mAttributes;
    }

    public AnalyticsEvent(String name) {
        this.mName = name;
    }

    public AnalyticsEvent(String name, String description) {
        this.mName = name;
        this.mAttributes.putString(DESCRIPTION, description);
    }
}
