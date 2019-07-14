package com.android.oleksandrpriadko.overlay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.overlay.HideMethod.BACK_PRESSED
import kotlinx.android.synthetic.main.overlay_module_activity.*

class OverlayTestActivity : AppCompatActivity() {

    private lateinit var overlayManager: OverlayManager

    private var idOfLastCreated = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overlay_module_activity)

        overlayManager = OverlayManager(layout_overlay_container)
        display.setOnClickListener { display() }
        popAll.setOnClickListener { popAll() }
    }

    private fun display() {
        overlayManager.add(createOverlayHolder())
    }

    private fun popAll() {
        overlayManager.hideAll()
        idOfLastCreated = -1
    }

    private fun createOverlayHolder(): Overlay {
        val id: String = idOfLastCreated.toString()
        if (idOfLastCreated == -1) {
            idOfLastCreated = 0
        } else {
            idOfLastCreated++
        }

        return Overlay.Builder(createViewGroup(), id)
                .setHideByClickOnBackground(true)

                .backgroundView(R.id.back)
                .animationShowBackground(R.anim.overlay_module_slide_up)
                .animationHideBackground(R.anim.overlay_module_slide_down)

                .contentView(R.id.content)
                .animationShowContent(R.anim.overlay_module_slide_up)
                .animationHideContent(R.anim.overlay_module_slide_down)


                .build()
    }

    private fun createViewGroup(): ViewGroup {
        val viewGroupOverlay = LayoutInflater.from(this).inflate(
                R.layout.overlay_module_layout_overlay,
                layout_overlay_container,
                false) as ViewGroup

        randomResize(viewGroupOverlay.findViewById(R.id.back), viewGroupOverlay.findViewById(R.id.content))

        return viewGroupOverlay
    }

    private fun randomResize(backView: View, contentView: View) {
        val layoutParamsFromBack = backView.layoutParams
        val heightFromBack = if (layoutParamsFromBack.height < 3) 1500 else layoutParamsFromBack.height
        val widthFromBack = if (layoutParamsFromBack.width < 3) 1500 else layoutParamsFromBack.width

        val heightForBack = randomIntInRange(heightFromBack / 5, heightFromBack - heightFromBack / 5)
        val widthForBack = randomIntInRange(widthFromBack / 5, widthFromBack - widthFromBack / 5)

        layoutParamsFromBack.height = heightForBack
        layoutParamsFromBack.width = widthForBack

        val heightForContent = randomIntInRange(heightForBack / 5, heightForBack - heightForBack / 5)
        val widthForContent = randomIntInRange(widthForBack / 5, widthForBack - widthForBack / 5)

        val layoutParamsForContent = contentView.layoutParams
        layoutParamsForContent.height = heightForContent
        layoutParamsForContent.width = widthForContent

        backView.layoutParams = layoutParamsFromBack
        contentView.layoutParams = layoutParamsForContent
    }

    private fun randomIntInRange(min: Int, max: Int): Int {
        return (Math.random() * (max - min) + min).toInt()
    }

    override fun onBackPressed() {
        if (!overlayManager.hideLast(BACK_PRESSED)) {
            super.onBackPressed()
        } else {
            idOfLastCreated--

        }
    }

    override fun onResume() {
        super.onResume()
        overlayManager.resume()
    }

    override fun onStop() {
        overlayManager.stop()
        super.onStop()
    }
}
