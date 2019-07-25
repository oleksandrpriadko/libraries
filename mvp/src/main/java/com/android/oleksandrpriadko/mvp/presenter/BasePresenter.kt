package com.android.oleksandrpriadko.mvp.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import com.android.oleksandrpriadko.loggalitic.LogPublishService

abstract class BasePresenter<T : LifecycleOwner>(view: T?) : DefaultLifecycleObserver {

    protected var view: T? = view
        get() {
            field?.let {
                if (it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) || isViewInEditMode) {
                    return it
                }
            }
            return null
        }

    init {
        this.view?.let {
            bindView(it)
        }
    }

    fun bindView(view: T) {
        if (view.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            logState("dead lifecycle owner")
            return
        }
        logState("created")
        view.lifecycle.addObserver(this)

        this.view = view
    }

    private fun unbind() {
        view = null
        logState("view unbound")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        logState(owner.lifecycle.currentState.name)
        unbind()
    }

    protected fun enableLog(): Boolean = false

    private fun logState(message: String) {
        if (enableLog()) {
            LogPublishService.logger().d(javaClass.simpleName, message)
        }
    }

    open var isViewInEditMode = false
}