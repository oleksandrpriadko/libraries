package com.android.oleksandrpriadko.demo.item_decoration;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.oleksandrpriadko.demo.R;
import com.android.oleksandrpriadko.demo.adapter.AdapterDemos;
import com.android.oleksandrpriadko.demo.adapter.Demo;
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener;
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationVerticalGridMargin;
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationMargin;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ItemDecorationActivity extends AppCompatActivity {

    private static final int SPAN_COUNT = 3;
    @BindView(R.id.button_layout_manager) Button mButtonLayoutManager;
    @BindView(R.id.button_margin) Button mButtonMargin;
    @BindView(R.id.checkbox_margin_first) CheckBox mCheckBoxFirstMargin;
    @BindView(R.id.checkbox_margin_last) CheckBox mCheckBoxLastMargin;
    @BindView(R.id.checkbox_include_edge) CheckBox mCheckBoxIncludeEdge;
    @BindView(R.id.recView) RecyclerView mRecyclerView;

    private ItemDecorationMargin mItemDecorationMargin;
    private ItemDecorationVerticalGridMargin mItemDecorationVerticalGridMargin;
    private Options mCurrentOptions = new Options();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_decoration);

        ButterKnife.bind(this);

        createAdapter();

        applyOptions(mCurrentOptions);
    }

    private void createAdapter() {
        AdapterDemos adapterDemos = new AdapterDemos(new BaseItemListener<Demo>() {
            @Override
            public void isEmpty(boolean b) {

            }

            @Override
            public void itemClicked(int i, Demo demo) {

            }
        });

        List<Demo> demoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Demo demo = new Demo(null, "Dummy " + i, null);
            demoList.add(demo);
        }
        adapterDemos.setData(demoList);
        mRecyclerView.setAdapter(adapterDemos);
        adapterDemos.notifyDataSetChanged();
    }

    @OnClick({R.id.button_layout_manager, R.id.button_margin, R.id.button_orientation})
    void click(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        switch (view.getId()) {
            case R.id.button_layout_manager:
                popupMenu.inflate(R.menu.item_decoration_layout_manager);
                break;
            case R.id.button_margin:
                popupMenu.inflate(R.menu.item_decoration_margin);
                break;
            case R.id.button_orientation:
                popupMenu.inflate(R.menu.item_decoration_orientation);
                break;
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
    }

    @OnCheckedChanged({R.id.checkbox_margin_last, R.id.checkbox_margin_first, R.id.checkbox_include_edge})
    void checked(CheckBox checkBox, boolean checked) {
        switch (checkBox.getId()) {
            case R.id.checkbox_margin_first:
                mCurrentOptions.setFirstMargin(checked);
                break;
            case R.id.checkbox_margin_last:
                mCurrentOptions.setLastMargin(checked);
                break;
                case R.id.checkbox_include_edge:
                mCurrentOptions.setIncludeEdge(checked);
                break;
        }
        applyOptions(mCurrentOptions);
    }

    private void applyOptions(Options options) {
        RecyclerView.LayoutManager layoutManager = null;
        switch (options.getManagerType()) {
            case Options.LINEAR:
                layoutManager = new LinearLayoutManager(this);
                ((LinearLayoutManager) layoutManager).setOrientation(options.getOrientation());
                break;
            case Options.GRID:
                layoutManager = new GridLayoutManager(this, SPAN_COUNT);
                ((GridLayoutManager) layoutManager).setOrientation(options.getOrientation());
                break;
        }
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.removeItemDecoration(mItemDecorationMargin);
        mRecyclerView.removeItemDecoration(mItemDecorationVerticalGridMargin);
        switch (options.getManagerType()) {
            case Options.GRID:
                mItemDecorationVerticalGridMargin = new ItemDecorationVerticalGridMargin(
                    SPAN_COUNT,
                    options.getMargin(),
                    options.isIncludeEdge());
                mRecyclerView.addItemDecoration(mItemDecorationVerticalGridMargin);
                break;
            case Options.LINEAR:
                mItemDecorationMargin = new ItemDecorationMargin(
                    options.getMargin(),
                    options.isFirstMargin(),
                    options.isLastMargin(),
                    options.getOrientation());
                mRecyclerView.addItemDecoration(mItemDecorationMargin);
                break;
        }

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener = item -> {
        switch (item.getItemId()) {
            case R.id.linear:
                mCurrentOptions.setManagerType(Options.LINEAR);
                break;
            case R.id.grid:
                mCurrentOptions.setManagerType(Options.GRID);
                break;
            case R.id.dp10:
                mCurrentOptions.setMargin(10);
                break;
            case R.id.dp100:
                mCurrentOptions.setMargin(100);
                break;
            case R.id.dp300:
                mCurrentOptions.setMargin(300);
                break;
            case R.id.vertical:
                mCurrentOptions.setOrientation(RecyclerView.VERTICAL);
                break;
            case R.id.horizontal:
                mCurrentOptions.setOrientation(RecyclerView.HORIZONTAL);
                break;
        }
        applyOptions(mCurrentOptions);
        return false;
    };
}
