package com.android.oleksandrpriadko.demo;

import com.android.oleksandrpriadko.demo.loggalitic.LoggaliticDemoActivity;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.oleksandrpriadko.demo.adapter.AdapterDemos;
import com.android.oleksandrpriadko.demo.adapter.Demo;
import com.android.oleksandrpriadko.demo.item_decoration.ItemDecorationActivity;
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener;
import com.android.oleksandrpriadko.recycler_decoration.ItemDecorationMargin;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recView)
    RecyclerView mRecyclerViewDemos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        intRecViewDemos();
    }

    private void intRecViewDemos() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewDemos.setLayoutManager(layoutManager);

        AdapterDemos adapterDemos = createAdapter();
        mRecyclerViewDemos.setAdapter(adapterDemos);

        ItemDecorationMargin itemDecorationMargin = new ItemDecorationMargin(
            30,
            true,
            true,
            layoutManager.getOrientation());
        mRecyclerViewDemos.addItemDecoration(itemDecorationMargin);
    }

    private AdapterDemos createAdapter() {
        BaseItemListener<Demo> baseItemListener = new BaseItemListener<Demo>() {
            @Override
            public void isEmpty(boolean isEmpty) {

            }

            @Override
            public void itemClicked(int position, Demo item) {
                Intent intent = new Intent(MainActivity.this, item.getClazz());
                startActivity(intent);
            }
        };
        AdapterDemos adapterDemos = new AdapterDemos(baseItemListener);
        List<Demo> demoList = new ArrayList<>();
        Demo demo = new Demo(
            ItemDecorationActivity.class,
            "ItemDecoration",
            null);
        demoList.add(demo);

        demo = new Demo(
            LoggaliticDemoActivity.class,
            "Loggalitic",
            null);
        demoList.add(demo);

        adapterDemos.setData(demoList);

        return adapterDemos;
    }
}
