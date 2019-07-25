package com.android.oleksandrpriadko.demo.cocktails.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.managers.Key
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity
import kotlinx.android.synthetic.main.activity_splash.*

class CocktailSplashActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        lottieAnimationView.progress = CocktailManagerFinder.sharedPreferencesManager
                .get(Key.LOTTIE_SPLASH_PROGRESS)

        handler.postDelayed({
            CocktailManagerFinder.sharedPreferencesManager.save(
                    lottieAnimationView.progress, Key.LOTTIE_SPLASH_PROGRESS)
            lottieAnimationView.cancelAnimation()
            startActivity(Intent(this, SearchActivity::class.java))
            finish()
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}