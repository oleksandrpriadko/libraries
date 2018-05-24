package com.android.oleksandrpriadko.mvp.presenter;

public abstract class BasePresenter<T> {
    private T mView;

    protected final T getView() {
        return mView;
    }

    protected final boolean isViewAdded() {
        return mView != null;
    }

    public final void bindView(T view) {
        this.mView = view;
    }

    public final void unBindView() {
        this.mView = null;
    }
}