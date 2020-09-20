package com.dengzii.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class EmptyViewHolder extends AbsViewHolder<Object> {

    public EmptyViewHolder(@NonNull ViewGroup parent) {
        super(parent);
    }

    @Override
    public void onCreate(@NonNull ViewGroup parent) {
        setContentView(R.layout.item_super_adapter_empty_data);
    }

    @Override
    public void onBindData(@NonNull Object data, int position) {

    }
}
