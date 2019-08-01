package com.android.oleksandrpriadko.demo.cocktails.splash

import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.search.SearchRepo
import com.android.oleksandrpriadko.demo.cocktails.search.SearchRepoListener
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class CocktailSplashPresenter(presenterView: PresenterView,
                              baseUrl: String)
    : BasePresenter<PresenterView>(presenterView) {

    private val repo: SearchRepo = SearchRepo(presenterView, baseUrl)
    private val handlerPostRequest: Handler = Handler()

    private fun loadAllIngredients() {
        handlerPostRequest.removeCallbacksAndMessages(null)
        view?.onPauseLottie()
        view?.onRestoreLottieProgress()
        view?.onPlayLottie()
        handlerPostRequest.postDelayed(runnableRequest, delayBeforeRequest)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadAllIngredients()
    }

    private val runnableRequest: Runnable = Runnable {
        repo.loadAllIngredients(object : SearchRepoListener {
            override fun onLoadingStarted() {}

            override fun onLoadingDone() {
                view?.onSaveLottieProgress()
                view?.onOpenSearchScreen()
                view?.onFinishScreen()
            }

            override fun onLoadingError() {}

            override fun onNoInternet() {}
        })
    }

    companion object {
        private const val delayBeforeRequest = 2000L
    }

}

interface PresenterView : LifecycleOwner {

    fun onRestoreLottieProgress()

    fun onSaveLottieProgress()

    fun onPlayLottie()

    fun onPauseLottie()

    fun onStopLottie()

    fun onOpenSearchScreen()

    fun onFinishScreen()

}