package com.dengzii.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuperDiffHelper extends DiffUtil.Callback {

    private List<?> mOldData;
    private List<?> mNewData;
    private SuperAdapter mAdapter;

    public SuperDiffHelper(SuperAdapter adapter) {
        this.mAdapter = adapter;
    }

    public DiffUtil.DiffResult update(List<?> oldData, List<?> newData) {
        this.mNewData = newData;
        this.mOldData = oldData;
        return DiffUtil.calculateDiff(this);
    }

    @Override
    public int getOldListSize() {
        return mOldData.size();
    }

    @Override
    public int getNewListSize() {
        return mNewData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Object old = getOrNull(mOldData, oldItemPosition);
        Object new_ = getOrNull(mNewData, newItemPosition);
        if (old == null || new_ == null) {
            return false;
        }
        return old.hashCode() == new_.hashCode();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object old = getOrNull(mOldData, oldItemPosition);
        Object new_ = getOrNull(mNewData, newItemPosition);
        if (old == null || new_ == null) {
            return false;
        }
        return old.equals(new_);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Object old = getOrNull(mOldData, oldItemPosition);
        Object new_ = getOrNull(mNewData, newItemPosition);
        if (old == null || new_ == null) {
            return null;
        }
        RecyclerView.ViewHolder viewHolder = mAdapter.getViewHolderFor(old);
        if (!(viewHolder instanceof AbsViewHolder)) {
            return null;
        }
        return ((AbsViewHolder<?>) viewHolder).diff(new_);
    }

    private Object getOrNull(List<?> data, int index) {
        if (data.size() > index) {
            return data.get(index);
        }
        return null;
    }
}
