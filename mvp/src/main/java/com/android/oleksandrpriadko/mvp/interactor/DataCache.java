package com.android.oleksandrpriadko.mvp.interactor;

import java.util.concurrent.TimeUnit;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DataCache<D> {

    private final Cache<String, D> mDataCache;
    private final String mKey;

    public DataCache(String key,
                     @IntRange(from = 1) int cacheSizeMb,
                     @IntRange(from = 1L) long expTime,
                     TimeUnit expTimeUnit) {
        this.mKey = key;
        this.mDataCache = CacheBuilder.newBuilder()
            .maximumSize(cacheSizeMb)
            .expireAfterWrite(expTime, expTimeUnit)
            .build();
    }

    @Nullable
    public final D getCachedData() {
        return this.mDataCache != null ? this.mDataCache.getIfPresent(mKey) : null;
    }

    public final void saveToCache(D data) {
        this.mDataCache.put(mKey, data);
    }

    public final void clearCache() {
        this.mDataCache.invalidateAll();
    }
}
