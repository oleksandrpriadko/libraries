package com.android.oleksandrpriadko.recycler_adapter

interface BaseItemListener<T> {
    fun isEmpty(isEmpty: Boolean)

    fun itemClicked(position: Int, item: T)
}
