package com.android.oleksandrpriadko.mvp.presenter;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.oleksandrpriadko.loggalitic.Loggalitic;

public abstract class BasePresenter<T extends LifecycleOwner> implements DefaultLifecycleObserver {

    private T mView;

    public BasePresenter(@NonNull T view) {
        bindView(view);
    }

    public final void bindView(@NonNull final T view) {
        if (view.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            logState("dead lifecycle owner");
            return;
        }
        logState("created");
        view.getLifecycle().addObserver(this);

        mView = view;
    }

    @Nullable
    protected final T getView() {
        return mView.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED) ? mView : null;
    }

    @Override
    public final void onDestroy(@NonNull LifecycleOwner owner) {
        logState("onDestroy");
        unbind();
    }

    private void unbind() {
        mView = null;
        logState("view destroyed");
    }

    private void logState(@NonNull final String message) {
        Loggalitic.logger().d(getClass().getSimpleName(), message);
    }
}