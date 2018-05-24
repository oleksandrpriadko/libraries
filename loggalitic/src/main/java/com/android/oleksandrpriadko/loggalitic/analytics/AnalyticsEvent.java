package com.android.oleksandrpriadko.loggalitic.analytics;

import android.os.Bundle;

/**
 * baselib
 * Created by Oleksandr Priadko.
 * 7/9/17
 *
 * Generic event which will be converted to necessary type.
 */

public class AnalyticsEvent {

    /**
     * Event name
     */
    private String name;
    /**
     * Event attributes
     */
    private final Bundle attributes = new Bundle();

    public String getName() {
        return name;
    }

    public Bundle getAttributes() {
        return attributes;
    }

    /**
     * Uses by {@link Publisher} implementations
     * @param name  event name
     */
    AnalyticsEvent prepareEvent(String name) {
        this.name = name;
        return this;
    }
    /**
     * Uses by {@link Publisher} implementations
     * @param name          event name
     * @param description   event details
     */
    AnalyticsEvent prepareEvent(String name, String description) {
        this.name = name;
        this.attributes.putString("description", description);
        return this;
    }
}
