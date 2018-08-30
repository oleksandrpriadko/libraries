package com.android.oleksandrpriadko.mvp.repo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.oleksandrpriadko.loggalitic.Loggalitic;

import static android.arch.lifecycle.Lifecycle.State.DESTROYED;

public abstract class ObservableRepo {

    private List<LifecycleBoundObserver> mLifecycleBoundObservers = new ArrayList<>();

    public ObservableRepo(@NonNull final LifecycleOwner lifecycleOwner) {
        requestObserve(lifecycleOwner);
    }

    @CallSuper
    public void requestObserve(LifecycleOwner lifecycleOwner) {
        if (lifecycleOwner.getLifecycle().getCurrentState() == DESTROYED) {
            logState("dead lifecycle owner");
            return;
        }

        if (isAlreadyAdded(lifecycleOwner)) {
            logState("already existing lifecycle owner");
            return;
        }

        LifecycleBoundObserver lifecycleBoundObserver = new LifecycleBoundObserver(lifecycleOwner);
        mLifecycleBoundObservers.add(lifecycleBoundObserver);
        logState("added");
    }

    private boolean isAlreadyAdded(@NonNull final LifecycleOwner incomingLifecycleOwner) {
        for (LifecycleBoundObserver lifecycleBoundObserver : mLifecycleBoundObservers) {
            if (lifecycleBoundObserver.getLifecycleOwner() != null
                && lifecycleBoundObserver.getLifecycleOwner().equals(incomingLifecycleOwner)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private LifecycleBoundObserver findObserver(@NonNull final LifecycleOwner lifecycleOwner) {
        for (LifecycleBoundObserver lifecycleBoundObserver : mLifecycleBoundObservers) {
            if (lifecycleBoundObserver.getLifecycleOwner() != null) {
                if (lifecycleBoundObserver.getLifecycleOwner().equals(lifecycleOwner)) {
                    return lifecycleBoundObserver;
                }
            }
        }
        return null;
    }

    @CallSuper
    public void requestRemoveObserver(LifecycleOwner lifecycleOwner) {
        LifecycleBoundObserver lifecycleBoundObserver = findObserver(lifecycleOwner);
        if (lifecycleBoundObserver != null) {
            mLifecycleBoundObservers.remove(lifecycleBoundObserver);
            logState("removed");
        } else {
            logState("not found, nothing to remove");
        }

        cleanUpObservers();
        if (!hasObservers()) {
            logState("no observers, destroy " + mLifecycleBoundObservers.size());
            cleanUp();
        }
    }

    private void cleanUpObservers() {
        Iterator iterator = mLifecycleBoundObservers.iterator();
        while (iterator.hasNext()) {
            LifecycleBoundObserver lifecycleBoundObserver = (LifecycleBoundObserver) iterator.next();
            if (lifecycleBoundObserver == null || lifecycleBoundObserver.getLifecycleOwner() == null) {
                iterator.remove();
                logState("found nullable lifecycle owner during clean up");
            }
        }
        logState("observers cleaned up");
    }

    public boolean hasObservers() {
        return !mLifecycleBoundObservers.isEmpty();
    }

    public abstract void cleanUp();

    private void logState(@NonNull final String message) {
        Loggalitic.logger().d(getClass().getSimpleName(), message);
    }

    private final class LifecycleBoundObserver implements GenericLifecycleObserver {

        private final LifecycleOwner mLifecycleOwner;

        private LifecycleBoundObserver(@NonNull LifecycleOwner owner) {
            mLifecycleOwner = owner;
            mLifecycleOwner.getLifecycle().addObserver(this);
        }

        @Override
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            logState("onStateChanged " + event.name() + " " + mLifecycleOwner);
            if (mLifecycleOwner.getLifecycle().getCurrentState() == DESTROYED) {
                mLifecycleOwner.getLifecycle().removeObserver(this);
                logState("dead lifecycle owner " + mLifecycleOwner);

                requestRemoveObserver(mLifecycleOwner);
            }
        }

        private LifecycleOwner getLifecycleOwner() {
            return mLifecycleOwner;
        }
    }

}
