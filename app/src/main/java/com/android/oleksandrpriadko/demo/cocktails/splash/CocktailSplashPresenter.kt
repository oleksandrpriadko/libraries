package com.android.oleksandrpriadko.demo.cocktails.splash

import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import com.android.oleksandrpriadko.demo.cocktails.search.LoadingListener
import com.android.oleksandrpriadko.demo.cocktails.search.SearchRepo
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class CocktailSplashPresenter(presenterView: PresenterView,
                              baseUrl: String)
    : BasePresenter<PresenterView>(presenterView) {

    private val repo: SearchRepo = SearchRepo(presenterView, baseUrl)
    private val handlerPostRequest: Handler = Handler()

    fun loadAllIngredients() {
        handlerPostRequest.removeCallbacksAndMessages(null)
        view?.onPauseLottie()
        view?.onRestoreLottieProgress()
        view?.onPlayLottie()
        handlerPostRequest.postDelayed(runnableRequest, delayBeforeRequest)
    }

    fun onScreenStop() {

    }

    fun onScreenResume() {

    }

    private val runnableRequest: Runnable = Runnable {
        repo.loadListOfAllIngredients(object : LoadingListener {
            override fun onLoadingStarted() {}

            override fun onLoadingDone() {
                view?.onSaveLottieProgress()
                view?.onPauseLottie()
                view?.onOpenSearchScreen()
                view?.onFinishScreen()
            }

            override fun onListOfIngredientsLoaded(ingredientNames: MutableList<IngredientName>) {}

            override fun onLoadingError(throwable: Throwable) {}

            override fun onNoInternet() {}
        })
    }

    companion object {
        private const val delayBeforeRequest = 500L
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