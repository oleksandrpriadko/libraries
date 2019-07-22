package com.android.oleksandrpriadko.mvp.repo

import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import java.util.*

abstract class ObservableRepo(lifecycleOwner: LifecycleOwner) {

    private val lifecycleBoundObservers = ArrayList<LifecycleBoundObserver>()

    init {
        requestObserve(lifecycleOwner)
    }

    @CallSuper
    fun requestObserve(lifecycleOwner: LifecycleOwner) {
        if (lifecycleOwner.lifecycle.currentState == DESTROYED) {
            logState("dead lifecycle owner")
            return
        }

        if (isAlreadyAdded(lifecycleOwner)) {
            logState("already existing lifecycle owner")
            return
        }

        val lifecycleBoundObserver = LifecycleBoundObserver(lifecycleOwner)
        lifecycleBoundObservers.add(lifecycleBoundObserver)
        logState("added")
    }

    private fun isAlreadyAdded(incomingLifecycleOwner: LifecycleOwner): Boolean {
        for (lifecycleBoundObserver in lifecycleBoundObservers) {
            if (lifecycleBoundObserver.lifecycleOwner == incomingLifecycleOwner) {
                return true
            }
        }
        return false
    }

    private fun findObserver(lifecycleOwner: LifecycleOwner): LifecycleBoundObserver? {
        for (lifecycleBoundObserver in lifecycleBoundObservers) {
            if (lifecycleBoundObserver.lifecycleOwner === lifecycleOwner) {
                return lifecycleBoundObserver
            }
        }
        return null
    }

    @CallSuper
    fun requestRemoveObserver(lifecycleOwner: LifecycleOwner) {
        val lifecycleBoundObserver = findObserver(lifecycleOwner)
        if (lifecycleBoundObserver != null) {
            lifecycleBoundObservers.remove(lifecycleBoundObserver)
            logState("removed")
        } else {
            logState("not found, nothing to remove")
        }

        if (!hasObservers()) {
            logState("no observers, destroy " + lifecycleBoundObservers.size)
            cleanUp()
        }
    }

    fun hasObservers(): Boolean {
        return lifecycleBoundObservers.isNotEmpty()
    }

    abstract fun cleanUp()

    private fun logState(message: String) {
        LogPublishService.logger().d(javaClass.simpleName, message)
    }

    inner class LifecycleBoundObserver constructor(val lifecycleOwner: LifecycleOwner)
        : DefaultLifecycleObserver {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            logState("onStateChanged onDestroy() $lifecycleOwner")
            if (lifecycleOwner.lifecycle.currentState == DESTROYED) {
                lifecycleOwner.lifecycle.removeObserver(this)
                logState("dead lifecycle owner $lifecycleOwner")

                requestRemoveObserver(lifecycleOwner)
            }
        }
    }

}
