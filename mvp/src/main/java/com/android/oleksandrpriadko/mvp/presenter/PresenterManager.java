package com.android.oleksandrpriadko.mvp.presenter;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class PresenterManager {
    private static final int MAX_SIZE = 10;
    private static final long EXPIRATION_VALUE = 3;
    private static PresenterManager instance;

    private final Cache<String, BasePresenter> presenters;

    private PresenterManager() {
        presenters = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterWrite(EXPIRATION_VALUE, TimeUnit.MINUTES)
            .build();
    }

    public static PresenterManager getInstance() {
        if (instance == null) {
            instance = new PresenterManager();
        }
        return instance;
    }

    public void saveToCache(BasePresenter presenter, String key) {
        presenters.put(key, presenter);
    }

    public <P extends BasePresenter> P getFromCache(String key) {
        return (P) presenters.getIfPresent(key);
    }
}