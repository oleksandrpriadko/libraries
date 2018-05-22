package com.android.oleksandrpriadko.recycler_adapter;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.oleksandrpriadko.recycler_adapter.R;
import com.squareup.picasso.Picasso;

public class BaseHolderPicasso extends RecyclerView.ViewHolder {
    public BaseHolderPicasso(View itemView) {
        super(itemView);
    }

    protected void loadImage(String url,
                             ImageView imageView,
                             @DrawableRes int placeHolderId,
                             @DrawableRes int errorID) {
        getPicasso()
                .load(url)
                .placeholder(placeHolderId)
                .error(errorID)
                .into(imageView);
    }

    protected void loadImage(String url,
                             ImageView imageView,
                             @DrawableRes int placeHolderId) {
        getPicasso()
                .load(url)
                .placeholder(placeHolderId)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imageView);
    }

    private Picasso getPicasso() {
        Picasso picasso = Picasso.with(itemView.getContext());
        picasso.setIndicatorsEnabled(false);
        picasso.setLoggingEnabled(false);
        return picasso;
    }
}
