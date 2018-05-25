package com.android.oleksandrpriadko.mvp.interactor;

import java.util.concurrent.TimeUnit;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DataCache<D> {

    private final Cache<String, D> mDataCache;
    private final String mKey;

    private DataCache(String key,
                      @IntRange(from = 1L) long cacheSizeMb,
                      @IntRange(from = 0L) long expTime,
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

    public static class Builder {

        private final String mKey;
        private long mSizeMb;
        private TimeUnit mExpirationTimeUnit;
        private long mExpirationDuration;

        public Builder(String key) {
            mKey = key;
        }

        public Builder maxSizeMB(@IntRange(from = 1L) long sizeMb) {
            mSizeMb = sizeMb;
            return this;
        }

        public Builder expirationTime(TimeUnit expireTimeUnit,
                                      @IntRange(from = 0L) long expirationDuration) {
            mExpirationTimeUnit = expireTimeUnit;
            mExpirationDuration = expirationDuration;
            return this;
        }

        public <D> DataCache<D> build() {
            return new DataCache<>(mKey, mSizeMb, mExpirationDuration, mExpirationTimeUnit);
        }
    }
}
