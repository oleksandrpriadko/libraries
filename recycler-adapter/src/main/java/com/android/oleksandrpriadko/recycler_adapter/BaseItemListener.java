package com.android.oleksandrpriadko.recycler_adapter;

public interface BaseItemListener<T> {
    void isEmpty(boolean isEmpty);

    void itemClicked(int position, T item);
}
