package com.android.oleksandrpriadko.demo.cocktails.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity

class CocktailSplashActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        handler.postDelayed({
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