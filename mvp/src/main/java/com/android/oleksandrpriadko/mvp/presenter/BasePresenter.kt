package com.android.oleksandrpriadko.mvp.presenter

import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import com.android.oleksandrpriadko.loggalitic.LogPublishService

abstract class BasePresenter<T : LifecycleOwner>(view: T) : DefaultLifecycleObserver {

    private var runnableOnNewIntent: Runnable? = null
    private var runnableOnActivityResult: Runnable? = null
    private var runnableOnPendingAction: Runnable? = null

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

    @CallSuper
    override fun onCreate(owner: LifecycleOwner) {
        bindView(owner)
    }

    @CallSuper
    override fun onResume(owner: LifecycleOwner) {
        bindView(owner)
    }

    @CallSuper
    private fun bindView(owner: LifecycleOwner) {
        this.isViewBound = true
        this.logState(owner.lifecycle.currentState.name)
    }

    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        this.logState(owner.lifecycle.currentState.name)
        this.isViewBound = false
    }

    @CallSuper
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

    fun saveOnNewIntentRunnable(runnable: Runnable) {
        runnableOnNewIntent = runnable
    }

    protected fun consumeOnNewIntentRunnable() {
        runnableOnNewIntent?.run()
        runnableOnNewIntent = null
    }

    fun saveOnActivityResultRunnable(runnable: Runnable) {
        runnableOnActivityResult = runnable
    }

    protected fun consumeOnActivityResultRunnable() {
        runnableOnActivityResult?.run()
        runnableOnActivityResult = null
    }

    fun saveOnPendingActionRunnable(runnable: Runnable) {
        runnableOnPendingAction = runnable
    }

    protected fun consumeOnPendingActionRunnable() {
        runnableOnPendingAction?.run()
        runnableOnPendingAction = null
    }

    open var isViewInEditMode = false
}