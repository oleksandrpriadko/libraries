package com.android.oleksandrpriadko.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.adapter.AdapterDemos
import com.android.oleksandrpriadko.demo.adapter.Demo
import com.android.oleksandrpriadko.demo.item_decoration.ItemDecorationActivity
import com.android.oleksandrpriadko.demo.loggalitic.LoggaliticDemoActivity
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationMargin
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers())
        setContentView(R.layout.activity_main)

        intRecViewDemos()
    }

    private fun intRecViewDemos() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapterDemos = createAdapter()
        recyclerView.adapter = adapterDemos

        val itemDecorationMargin = ItemDecorationMargin(
                30,
                firstItemMargin = true,
                lastItemMargin = true,
                linearLayoutManagerOrientation = layoutManager.orientation)
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
                0)
        demoList.add(demo)

        demo = Demo(
                LoggaliticDemoActivity::class.java,
                "Loggalitic",
                null,
                0)
        demoList.add(demo)

        adapterDemos.setData(demoList)

        return adapterDemos
    }
}
