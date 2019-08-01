package com.android.oleksandrpriadko.demo.cocktails.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.managers.Key
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity
import kotlinx.android.synthetic.main.cocktail_activity_splash.*

class CocktailSplashActivity : AppCompatActivity(), PresenterView {

    private var presenter: CocktailSplashPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cocktail_activity_splash)

        presenter = CocktailSplashPresenter(this, getString(R.string.cocktail_base_url))
    }

    override fun onRestoreLottieProgress() {
        lottieAnimationView.progress = CocktailManagerFinder.sharedPreferencesManager
                .get(Key.LOTTIE_SPLASH_PROGRESS)
    }

    override fun onSaveLottieProgress() {
        CocktailManagerFinder.sharedPreferencesManager.save(
                lottieAnimationView.progress, Key.LOTTIE_SPLASH_PROGRESS)
    }

    override fun onPlayLottie() {
        lottieAnimationView.playAnimation()
    }

    override fun onPauseLottie() {
        lottieAnimationView.pauseAnimation()
    }

    override fun onStopLottie() {
        lottieAnimationView.cancelAnimation()
    }

    override fun onOpenSearchScreen() {
        SearchActivity.open(this)
    }

    override fun onFinishScreen() {
        finish()
    }
}