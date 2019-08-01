package com.android.oleksandrpriadko.mvp.repo_extension

import io.realm.Realm

abstract class RealmRepoExtension : RepoExtension {

    protected val realm: Realm? = Realm.getDefaultInstance()
        get() {
            return when {
                field?.isClosed == true -> {
                    logState("is already closed")
                    null
                }
                else -> field
            }
        }

    override fun cleanUp() {
        realm?.close()
    }
}