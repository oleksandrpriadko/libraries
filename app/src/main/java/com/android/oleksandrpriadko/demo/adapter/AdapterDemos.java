package com.android.oleksandrpriadko.demo.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.oleksandrpriadko.demo.R;
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView;
import com.android.oleksandrpriadko.recycler_adapter.BaseHolderPicasso;
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AdapterDemos extends BaseAdapterRecyclerView<Demo, AdapterDemos.Holder, BaseItemListener<Demo>> {

    public AdapterDemos(@NonNull BaseItemListener<Demo> itemListener) {
        super(itemListener);
    }

    @Override
    protected Holder onGetHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, int i) {
        return new Holder(layoutInflater.inflate(R.layout.item_demo, viewGroup, false));
    }

    @Override
    protected boolean isItemViewClickable() {
        return true;
    }

    @Override
    protected void onBindHolder(Holder holder, int i) {
        Demo demo = getData().get(i);
        holder.mTextViewName.setText(demo.getName());
        holder.loadAvatar(demo.getAvatarUrl());
    }

    class Holder extends BaseHolderPicasso {

        @BindView(R.id.textView_demo_name) TextView mTextViewName;
        @BindView(R.id.imageView_demo_avatar) ImageView mImageViewAvatar;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void loadAvatar(String url) {
            loadImage(url, mImageViewAvatar, R.drawable.ic_hexagon_black_24dp);
        }
    }
}