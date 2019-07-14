package com.android.oleksandrpriadko.demo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.adapter.AdapterDemos
import com.android.oleksandrpriadko.demo.adapter.Demo
import com.android.oleksandrpriadko.demo.item_decoration.ItemDecorationActivity
import com.android.oleksandrpriadko.demo.loggalitic.LogPublishActivity
import com.android.oleksandrpriadko.extension.dimenPixelSize
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationVerticalGridMargin
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main_concept.*
import java.util.*

class MainActivityConcept : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers())
        setContentView(R.layout.activity_main_concept)

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
                left = dimenPixelSize(R.dimen.margin_left_right_card),
                top = dimenPixelSize(R.dimen.margin_top_bottom_card),
                right = dimenPixelSize(R.dimen.margin_left_right_card),
                bottom = dimenPixelSize(R.dimen.margin_top_bottom_card),
                includeEdge = true
        )
        recyclerView.addItemDecoration(itemDecorationMargin)
    }

    private fun createAdapter(): AdapterDemos {
        val baseItemListener = object : BaseItemListener<Demo> {
            override fun isEmpty(isEmpty: Boolean) {

            }

            override fun itemClicked(position: Int, item: Demo) {
                val intent = Intent(this@MainActivityConcept, item.clazz)
                startActivity(intent)
            }
        }
        val adapterDemos = AdapterDemos(baseItemListener)
        val demoList = ArrayList<Demo>()
        var demo = Demo(
                ItemDecorationActivity::class.java,
                "ItemDecoration",
                null,
                R.drawable.ic_workflow_512)
        demoList.add(demo)

        demo = Demo(
                LogPublishActivity::class.java,
                "Analytics",
                null,
                R.drawable.ic_analytics_512)
        demoList.add(demo)

        adapterDemos.setData(demoList)

        return adapterDemos
    }

}