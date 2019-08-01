package com.android.oleksandrpriadko.mvp.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import com.android.oleksandrpriadko.loggalitic.LogPublishService

abstract class BasePresenter<T : LifecycleOwner>(view: T) : DefaultLifecycleObserver {

    var runnableOnNewIntent: Runnable? = null
    var runnableOnActivityResult: Runnable? = null

    private var isViewBound: Boolean = false

    protected var view: T? = view
        get() {
            field?.let {
                return when {
                    it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) && this.isViewBound
                            || this.isViewInEditMode -> it
                    else -> null
                }
            }
            return null
        }

    init {
        subscribeView(view)
    }

    private fun subscribeView(view: T) {
        if (view.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            logState("dead lifecycle owner")
        } else {
            view.lifecycle.addObserver(this)
            this.view = view
            logState("subscribed")
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        bindView(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        bindView(owner)
    }

    private fun bindView(owner: LifecycleOwner) {
        this.isViewBound = true
        this.logState(owner.lifecycle.currentState.name)
    }

    override fun onStop(owner: LifecycleOwner) {
        this.logState(owner.lifecycle.currentState.name)
        this.isViewBound = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        this.logState(owner.lifecycle.currentState.name)
        this.isViewBound = false
        this.view = null
    }

    protected fun enableLog(): Boolean = false

    protected fun logState(message: String) {
        if (enableLog()) {
            LogPublishService.logger().d(this::class.java.simpleName, message)
        }
    }

    open var isViewInEditMode = false
}