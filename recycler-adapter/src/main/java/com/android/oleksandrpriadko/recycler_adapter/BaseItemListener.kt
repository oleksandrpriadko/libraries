package com.android.oleksandrpriadko.recycler_adapter

interface BaseItemListener<T> {
    fun isEmpty(isEmpty: Boolean)

    fun itemClicked(position: Int, item: T)
}

abstract class BaseItemListenerAdapter<T>  : BaseItemListener<T>{
    override fun isEmpty(isEmpty: Boolean) {}

    override fun itemClicked(position: Int, item: T) {}
}
