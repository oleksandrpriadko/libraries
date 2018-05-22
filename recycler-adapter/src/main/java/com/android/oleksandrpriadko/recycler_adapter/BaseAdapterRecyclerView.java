package com.android.oleksandrpriadko.recycler_adapter;

import java.util.List;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapterRecyclerView
        <D,
        H extends RecyclerView.ViewHolder,
        I extends BaseItemListener<D>>
        extends RecyclerView.Adapter<H> {

    private I itemListener;
    private List<D> items;
    private LayoutInflater inflater;

    public BaseAdapterRecyclerView(@NonNull I itemListener, List<D> data, boolean emptyFromConstructor) {
        this.itemListener = itemListener;
        items = data;
        if (emptyFromConstructor) {
            this.itemListener.isEmpty(isEmpty());
        }
    }

    public BaseAdapterRecyclerView(@NonNull I itemListener) {
        this.itemListener = itemListener;
    }

    public I getItemListener() {
        return itemListener;
    }

    public void setData(List<D> data) {
        this.items = data;
        if (isEmpty()){
            this.itemListener.isEmpty(true);
            return;
        }
        this.itemListener.isEmpty(isEmpty());
        this.notifyDataSetChanged();
    }

    protected void removeDataItem(int position){
        getData().remove(position);
        this.itemListener.isEmpty(isEmpty());
        this.notifyDataSetChanged();
    }

    protected void addDataItem(D item){
        if (item != null){
            getData().add(item);
            this.itemListener.isEmpty(isEmpty());
            this.notifyDataSetChanged();
        }
    }

    public List<D> getData() {
        return this.items;
    }

    private boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return onGetHolder(inflater, parent, viewType);
    }

    protected abstract H onGetHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull final H holder, int position) {
        if (isItemViewClickable()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    itemListener.itemClicked(position, getData().get(position));
                }
            });
        }
        this.onBindHolder(holder, position);
    }

    protected abstract boolean isItemViewClickable();

    protected abstract void onBindHolder(H holder, int position);

    @Override
    public int getItemCount() {
        return isEmpty() ? 0 : items.size();
    }
}
