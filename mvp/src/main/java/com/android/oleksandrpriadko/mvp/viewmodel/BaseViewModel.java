package com.android.oleksandrpriadko.mvp.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.android.oleksandrpriadko.mvp.presenter.BasePresenter;

public class BaseViewModel extends ViewModel implements LifecycleObserver {

    private BasePresenter mBasePresenter;
    private BasePresenter.PresenterView mPresenterView;

    BaseViewModel(@NonNull final Lifecycle lifecycle,
                         @NonNull final BasePresenter basePresenter,
                         @NonNull final BasePresenter.PresenterView presenterView) {
        lifecycle.addObserver(this);
        mBasePresenter = basePresenter;
        mPresenterView = presenterView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void bind() {
        mBasePresenter.bindView(mPresenterView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void unbind() {
        mBasePresenter.unBindView();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mBasePresenter = null;
        mPresenterView = null;
    }
}
