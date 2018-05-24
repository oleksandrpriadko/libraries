package com.android.oleksandrpriadko.demo.adapter;

public class Demo {

    private Class mClazz;
    private String mName;
    private String mAvatarUrl;

    public Demo(Class clazz, String name, String avatarUrl) {
        mClazz = clazz;
        mName = name;
        mAvatarUrl = avatarUrl;
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
}