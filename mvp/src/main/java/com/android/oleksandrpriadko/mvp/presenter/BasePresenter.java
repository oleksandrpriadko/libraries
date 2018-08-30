package com.android.oleksandrpriadko.mvp.presenter;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.oleksandrpriadko.loggalitic.Loggalitic;

public abstract class BasePresenter<T> implements DefaultLifecycleObserver {

    private T mView;

    private boolean mBound;

    public BasePresenter(T view, @NonNull final LifecycleOwner lifecycleOwner) {
        if (view == null) {
            throw new NullPointerException("View should not be null");
        }
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            logState("dead lifecycle owner");
            return;
        }
        logState("created");
        lifecycleOwner.getLifecycle().addObserver(this);
        mView = view;
        bind(true);
    }

    @Override
    @CallSuper
    public void onResume(@NonNull LifecycleOwner owner) {
        logState("onResume");
        bind(true);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        logState("onStop");
        bind(false);
    }

    @Override
    public final void onDestroy(@NonNull LifecycleOwner owner) {
        logState("onDestroy");
        bind(false);
        mView = null;
        logState("view destroyed");
    }

    protected final void bind(boolean bind) {
        mBound = bind;
        logState("bind " + bind);
    }

    @Nullable
    protected final T getView() {
        return mBound ? mView : null;
    }

    @NonNull
    protected final T getViewSafely() {
        return mView;
    }

    private void logState(@NonNull final String message) {
        Loggalitic.logger().d(getClass().getSimpleName(), message);
    }
}