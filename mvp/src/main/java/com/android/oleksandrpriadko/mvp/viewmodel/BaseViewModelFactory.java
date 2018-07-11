package com.android.oleksandrpriadko.mvp.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.android.oleksandrpriadko.mvp.presenter.BasePresenter;

public class BaseViewModelFactory implements ViewModelProvider.Factory {

    private final Lifecycle mLifecycle;
    private BasePresenter mCurrentPresenter;
    private BasePresenter.PresenterView mCurrentPresenterView;

    public BaseViewModelFactory(@NonNull final Lifecycle lifecycle) {
        mLifecycle = lifecycle;
    }

    public void setCurrentPresenterAndView(@NonNull final BasePresenter currentBasePresenter,
                                           @NonNull final BasePresenter.PresenterView currentPresenterView) {
        mCurrentPresenter = currentBasePresenter;
        mCurrentPresenterView = currentPresenterView;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T baseViewModel;
        if (modelClass.isAssignableFrom(BaseViewModel.class)) {
            baseViewModel = (T) new BaseViewModel(mLifecycle, mCurrentPresenter, mCurrentPresenterView);
            return baseViewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
