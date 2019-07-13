package com.android.oleksandrpriadko.demo.adapter;

import android.support.annotation.DrawableRes;

public class Demo {

    private Class mClazz;
    private String mName;
    private String mAvatarUrl;
    @DrawableRes
    private int iconResId;

    public Demo(Class clazz, String name, String avatarUrl, final int iconResId) {
        mClazz = clazz;
        mName = name;
        mAvatarUrl = avatarUrl;
        this.iconResId = iconResId;
    }

    public Class getClazz() {
        return mClazz;
    }

    public String getName() {
        return mName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public int getIconResId() {
        return iconResId;
    }
}