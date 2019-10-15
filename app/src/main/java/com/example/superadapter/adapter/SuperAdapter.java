package com.xht.kuaiyouyi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

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
public class SuperAdapter extends RecyclerView.Adapter<AbsViewHolder> {

    private List<?> mItems;
    private Context mContext;
    private SparseArray<Class<? extends AbsViewHolder>> mItemViewHolderForType;
    private SparseArray<IViewHolderGenerator> mHolderGenerators;
    private SparseArray<Class<?>> mTypes;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private View.OnClickListener mItemClickListener = v -> {
        if (mOnItemClickListener != null && v.getTag() != null) {
            AbsViewHolder.ItemInfo itemInfo = ((AbsViewHolder.ItemInfo) v.getTag());
            mOnItemClickListener.onItemClick(itemInfo.source,
                    itemInfo.data, itemInfo.position);
        }
    };

    private View.OnLongClickListener mItemLongClickListener = v -> {
        if (mOnItemLongClickListener != null && v.getTag() != null) {
            AbsViewHolder.ItemInfo itemInfo = ((AbsViewHolder.ItemInfo) v.getTag());
            return mOnItemLongClickListener.onItemClick(itemInfo.source,
                    itemInfo.data, itemInfo.position);
        }
        return false;
    };

    public SuperAdapter(List<?> data) {
        this(null, data);
    }

    public SuperAdapter(Context context, List<?> data) {
        mItems = data;
        mContext = context;
        mItemViewHolderForType = new SparseArray<>();
        mHolderGenerators = new SparseArray<>();
        mTypes = new SparseArray<>();
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
    public AbsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (null != mHolderGenerators.get(i, null)) {
            return mHolderGenerators.get(i).onCreateViewHolder(viewGroup);
        }
        if (null != mItemViewHolderForType.get(i, null)) {
            return getHolder(mItemViewHolderForType.get(i), viewGroup, i);
        }
        throw new RuntimeException(
                "No IViewHolderGenerator or AbsViewHolder found for item type " + mTypes.get(i)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AbsViewHolder absViewHolder, int position) {
        Object data = mItems.get(position);
        absViewHolder.setOnClickListener(mItemClickListener);
        absViewHolder.setOnLongClickListener(mItemLongClickListener);
        absViewHolder.onBindViewHolder(this, mItems, data, position);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getClass().hashCode();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
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
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
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

    public interface OnItemClickListener {
        void onItemClick(View source, Object itemData, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View source, Object itemData, int position);
    }

    public interface IViewHolderGenerator {
        @NonNull
        AbsViewHolder onCreateViewHolder(ViewGroup parent);
    }
}
