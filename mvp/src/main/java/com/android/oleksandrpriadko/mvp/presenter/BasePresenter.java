package com.android.oleksandrpriadko.mvp.presenter;

public abstract class BasePresenter {

    private PresenterView mView;

    protected final PresenterView getView() {
        return mView;
    }

    protected final boolean isViewAdded() {
        return mView != null;
    }

    public final void bindView(PresenterView view) {
        this.mView = view;
    }

    public final void unBindView() {
        this.mView = null;
    }

    /* no-op */
    public interface PresenterView {}
}