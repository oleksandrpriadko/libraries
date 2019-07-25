package com.android.oleksandrpriadko.demo.item_decoration

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.main.adapter.AdapterDemos
import com.android.oleksandrpriadko.demo.main.adapter.Demo
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationMargin
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationVerticalGridMargin
import kotlinx.android.synthetic.main.main_activity_item_decoration.*
import java.util.*

class ItemDecorationActivity : AppCompatActivity() {

    private var marginItemDecoration: ItemDecorationMargin? = null
    private var verticalGridMarginItemDecoration: ItemDecorationVerticalGridMargin? = null
    private val currentOptions = Options()

    private val onMenuItemClickListener = object : android.widget.PopupMenu.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.let { item.itemId }) {
                R.id.linear -> currentOptions.managerType = Options.LINEAR
                R.id.grid -> currentOptions.managerType = Options.GRID
                R.id.dp10 -> currentOptions.margin = 10
                R.id.dp100 -> currentOptions.margin = 100
                R.id.dp300 -> currentOptions.margin = 300
                R.id.vertical -> currentOptions.orientation = RecyclerView.VERTICAL
                R.id.horizontal -> currentOptions.orientation = RecyclerView.HORIZONTAL
            }
            applyOptions(currentOptions)
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_item_decoration)

        createAdapter()

        applyOptions(currentOptions)

        layoutManagerButton.setOnClickListener { it?.let { click(it) } }
        marginButton.setOnClickListener { it?.let { click(it) } }
        button_orientation.setOnClickListener { it?.let { click(it) } }

        marginLastCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView?.let { checked(buttonView, isChecked) }
        }
        marginFirstCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView?.let { checked(buttonView, isChecked) }
        }
        includeEdgeCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView?.let { checked(buttonView, isChecked) }
        }
    }

    private fun createAdapter() {
        val adapterDemos = AdapterDemos(object : BaseItemListener<Demo> {
            override fun isEmpty(isEmpty: Boolean) {

            }

            override fun itemClicked(position: Int, item: Demo) {

            }
        })

        val demoList = ArrayList<Demo>()
        for (i in 0..9) {
            val demo = Demo(null,
                    "Dummy $i",
                    null,
                    R.drawable.main_ic_gears_512)
            demoList.add(demo)
        }
        adapterDemos.setData(demoList)
        recyclerView.adapter = adapterDemos
        adapterDemos.notifyDataSetChanged()
    }

    private fun click(view: View) {
        val popupMenu = PopupMenu(this, view)
        when (view.id) {
            R.id.layoutManagerButton -> popupMenu.inflate(R.menu.main_item_decoration_layout_manager)
            R.id.marginButton -> popupMenu.inflate(R.menu.main_item_decoration_margin)
            R.id.button_orientation -> popupMenu.inflate(R.menu.main_item_decoration_orientation)
        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener)
    }

    private fun checked(checkBox: CompoundButton, checked: Boolean) {
        when (checkBox.id) {
            R.id.marginFirstCheckbox -> currentOptions.isFirstMargin = checked
            R.id.marginLastCheckbox -> currentOptions.isLastMargin = checked
            R.id.includeEdgeCheckbox -> currentOptions.isIncludeEdge = checked
        }
        applyOptions(currentOptions)
    }

    private fun applyOptions(options: Options) {
        var layoutManager: LinearLayoutManager? = null
        when (options.managerType) {
            Options.LINEAR -> {
                layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = options.orientation
            }
            Options.GRID -> {
                layoutManager = GridLayoutManager(this, SPAN_COUNT)
                layoutManager.orientation = options.orientation
            }
        }
        recyclerView.layoutManager = layoutManager
        marginItemDecoration?.let { recyclerView.removeItemDecoration(it) }
        verticalGridMarginItemDecoration?.let { recyclerView.removeItemDecoration(it) }
        when (options.managerType) {
            Options.GRID -> {
                verticalGridMarginItemDecoration = ItemDecorationVerticalGridMargin(
                        SPAN_COUNT,
                        options.margin,
                        options.isIncludeEdge)
                recyclerView.addItemDecoration(verticalGridMarginItemDecoration!!)
            }
            Options.LINEAR -> {
                marginItemDecoration = ItemDecorationMargin(
                        options.margin,
                        options.isFirstMargin,
                        options.isLastMargin,
                        options.orientation)
                recyclerView.addItemDecoration(marginItemDecoration!!)
            }
        }

        recyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {

        private const val SPAN_COUNT = 3
    }
}
