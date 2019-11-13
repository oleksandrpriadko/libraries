package com.android.oleksandrpriadko.demo.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.main.adapter.AdapterDemos
import com.android.oleksandrpriadko.demo.main.adapter.Demo
import com.android.oleksandrpriadko.demo.item_decoration.ItemDecorationActivity
import com.android.oleksandrpriadko.demo.logpublish.LogPublishActivity
import com.android.oleksandrpriadko.extension.dimenPixelSize
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationVerticalGridMargin
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.main_activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers())
        setContentView(R.layout.main_activity_main)

        intRecViewDemos()
    }

    private fun intRecViewDemos() {
        val spanCount = 2
        val layoutManager = GridLayoutManager(this, spanCount)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapterDemos = createAdapter()
        recyclerView.adapter = adapterDemos

        val itemDecorationMargin = ItemDecorationVerticalGridMargin(
                spanCount = spanCount,
                left = dimenPixelSize(R.dimen.main_margin_left_right_card),
                top = dimenPixelSize(R.dimen.main_margin_top_bottom_card),
                right = dimenPixelSize(R.dimen.main_margin_left_right_card),
                bottom = dimenPixelSize(R.dimen.main_margin_top_bottom_card),
                includeEdge = true
        )
        recyclerView.addItemDecoration(itemDecorationMargin)
    }

    private fun createAdapter(): AdapterDemos {
        val baseItemListener = object : BaseItemListener<Demo> {
            override fun isEmpty(isEmpty: Boolean) {

            }

            override fun itemClicked(position: Int, item: Demo) {
                val intent = Intent(this@MainActivity, item.clazz)
                startActivity(intent)
            }
        }
        val adapterDemos = AdapterDemos(baseItemListener)
        val demoList = ArrayList<Demo>()
        var demo = Demo(
                ItemDecorationActivity::class.java,
                "ItemDecoration",
                null,
                R.drawable.main_ic_workflow_512)
        demoList.add(demo)

        demo = Demo(
                LogPublishActivity::class.java,
                "Analytics",
                null,
                R.drawable.main_ic_analytics_512)
        demoList.add(demo)

        adapterDemos.setData(demoList)

        return adapterDemos
    }
}