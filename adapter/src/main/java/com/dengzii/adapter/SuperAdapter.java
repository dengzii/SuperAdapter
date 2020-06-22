package com.dengzii.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * author : dengzi
 * e-mail : denua@foxmail.com
 * time   : 2019/07/09 10:09
 * desc   : none
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class SuperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final Object HEADER = new Header();
    public static final Object FOOTER = new Footer();
    public static final Object EMPTY = new Empty();

    private static final SparseArray<Class<? extends AbsViewHolder>>
            DEFAULT_VIEW_HOLDER_FOR_TYPE = new SparseArray<>();

    private static final SparseArray<Class<?>> DEFAULT_DATA_TYPE = new SparseArray<>();

    private List<Object> mDataSet;
    private RecyclerView mRecyclerView = null;
    private Context mContext;
    private SparseArray<Class<? extends AbsViewHolder>> mItemViewHolderForType;
    private SparseArray<IViewHolderGenerator> mHolderGenerators;
    private SparseArray<Class<?>> mTypes;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    // add EMPTY item to data set when data set is empty.
    private boolean mEnableEmptyView = false;

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null && v.getTag() != null) {
                AbsViewHolder.ItemInfo itemInfo = ((AbsViewHolder.ItemInfo) v.getTag());
                mOnItemClickListener.onItemClick(itemInfo.source,
                        itemInfo.data, itemInfo.position, itemInfo.other);
            }
        }
    };

    private View.OnLongClickListener mItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null && v.getTag() != null) {
                AbsViewHolder.ItemInfo itemInfo = ((AbsViewHolder.ItemInfo) v.getTag());
                return mOnItemLongClickListener.onItemClick(itemInfo.source,
                        itemInfo.data, itemInfo.position);
            }
            return false;
        }
    };

    public SuperAdapter(List<?> data) {
        //noinspection unchecked
        mDataSet = (List<Object>) data;
        mItemViewHolderForType = new SparseArray<>();
        mHolderGenerators = new SparseArray<>();
        mTypes = new SparseArray<>();
    }

    public static void addDefaultViewHolderForType(Class<?> type, Class<? extends AbsViewHolder> holder) {
        DEFAULT_VIEW_HOLDER_FOR_TYPE.put(type.hashCode(), holder);
        DEFAULT_DATA_TYPE.put(type.hashCode(), holder);
    }

    public void setItemData(List<Object> data) {
        mDataSet = data;
    }

    public void addItemData(List<Object> data) {
        mDataSet.addAll(data);
    }

    public void setEnableEmptyView(boolean enableEmptyView) {
        mEnableEmptyView = enableEmptyView;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof AbsViewHolder) {
            ((AbsViewHolder) holder).onRecycled();
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.mOnItemLongClickListener = longClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void addViewHolderForType(Class<?> type, Class<? extends AbsViewHolder> holder) {
        mItemViewHolderForType.put(type.hashCode(), holder);
        mTypes.put(type.hashCode(), type);
    }

    public void addViewHolderGenerator(Class<?> type, IViewHolderGenerator generator) {
        mHolderGenerators.put(type.hashCode(), generator);
        mTypes.put(type.hashCode(), type);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AbsViewHolder viewHolder = null;
        if (null != mHolderGenerators.get(i, null)) {
            viewHolder = mHolderGenerators.get(i).onCreateViewHolder(viewGroup);
        } else if (null != mItemViewHolderForType.get(i, null)) {
            viewHolder = getHolder(mItemViewHolderForType.get(i), viewGroup, i);
        } else if (null != DEFAULT_VIEW_HOLDER_FOR_TYPE.get(i, null)) {
            viewHolder = getHolder(DEFAULT_VIEW_HOLDER_FOR_TYPE.get(i), viewGroup, i);
        }
        if (viewHolder == null) {
            viewHolder = new DefaultViewHolder(viewGroup);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof AbsViewHolder) {
            AbsViewHolder absViewHolder = ((AbsViewHolder) viewHolder);
            Object data = mDataSet.get(position);
            absViewHolder.setOnClickListener(mItemClickListener);
            absViewHolder.setOnLongClickListener(mItemLongClickListener);
            absViewHolder.onBindViewHolder(this, mDataSet, data, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getClass().hashCode();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private ViewTreeObserver.OnGlobalLayoutListener
            mRecyclerViewTreeObserver = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (mEnableEmptyView) {
                updateEmptyView();
            }
        }
    };

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mRecyclerViewTreeObserver);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(mRecyclerViewTreeObserver);
        mRecyclerView = null;
    }

    private void updateEmptyView() {
        if (mDataSet.isEmpty()) {
            mDataSet.add(EMPTY);
            super.notifyDataSetChanged();
        }
        if (!mDataSet.isEmpty() && mDataSet.size() > 1) {
            int indexOfEmpty = mDataSet.indexOf(EMPTY);
            if (indexOfEmpty >= 0) {
                mDataSet.remove(indexOfEmpty);
                super.notifyItemRemoved(indexOfEmpty);
            }
        }
    }

    private AbsViewHolder getHolder(Class<? extends AbsViewHolder> clazz,
                                    ViewGroup parent, int type) {
        Object result = null;
        try {
            for (Constructor c : clazz.getDeclaredConstructors()) {
                c.setAccessible(true);
                result = getViewHolder(c, parent);
                if (result != null) {
                    break;
                }
            }
            if (result == null) {
                throw new RuntimeException(
                        "No suitable constructor find with ViewHolder " + clazz.getName()
                );
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return (AbsViewHolder) result;
    }

    private Object getViewHolder(Constructor c, View parent)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {

        int parameterCount = c.getParameterTypes().length;

        boolean isInnerClass = parameterCount == 2 && c.getParameterTypes()[1] == ViewGroup.class;
        boolean isPublicClass = parameterCount == 1 && c.getParameterTypes()[0] == ViewGroup.class;
        if (isInnerClass) {
            return c.newInstance(null, parent);
        }
        if (isPublicClass) {
            return c.newInstance(parent);
        }
        return null;
    }

    private static final class Header {
    }

    private static final class Footer {
    }

    private static final class Empty {
    }

    public interface OnItemClickListener {
        void onItemClick(View source, Object itemData, int position, Object other);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View source, Object itemData, int position);
    }

    public interface IViewHolderGenerator {
        @NonNull
        AbsViewHolder onCreateViewHolder(ViewGroup parent);
    }
}
