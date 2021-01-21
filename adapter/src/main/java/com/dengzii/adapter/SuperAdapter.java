package com.dengzii.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
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
@SuppressWarnings({"unused"})
public class SuperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final Object EMPTY = new Empty();
    private static final SparseArray<Class<? extends AbsViewHolder<?>>>
            DEFAULT_VIEW_HOLDER_FOR_TYPE = new SparseArray<>();

    private final SparseArray<Class<? extends AbsViewHolder<?>>> mItemViewHolderForType;
    private final SparseArray<IViewHolderGenerator<?>> mHolderGenerators;
    private DataObserver mDataObserver = null;
    private RecyclerView mRecyclerView = null;
    private List<Object> mDataSet;
    private Object mHeader = null;
    private Object mFooter = null;
    private OnItemClickListener mOnItemClickListener;

    private final View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null && v.getTag() != null) {
                AbsViewHolder.ItemInfo itemInfo = ((AbsViewHolder.ItemInfo) v.getTag());
                mOnItemClickListener.onItemClick(itemInfo.source,
                        itemInfo.data, itemInfo.position, itemInfo.other);
            }
        }
    };
    private OnItemLongClickListener mOnItemLongClickListener;
    private final View.OnLongClickListener mItemLongClickListener = new View.OnLongClickListener() {
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
    /**
     * The item data add to data set when data is empty and empty view is enabled.
     */
    private Object mEmptyData = EMPTY;
    private boolean mFirstTimeLoadData = true;
    private boolean mEnableEmptyView = false;
    private boolean mEnableEmptyViewOnInit = true;
    private final ViewTreeObserver.OnGlobalLayoutListener mRecyclerViewTreeObserver =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mEnableEmptyView
                            && mEmptyData != null
                            && mEnableEmptyViewOnInit || !mFirstTimeLoadData
                    ) {
                        updateEmptyView();
                    }
                }
            };

    public SuperAdapter(List<?> data) {
        //noinspection unchecked
        mDataSet = (List<Object>) data;
        mItemViewHolderForType = new SparseArray<>();
        mHolderGenerators = new SparseArray<>();
    }

    public static void addDefaultViewHolderForType(Class<?> type, Class<? extends AbsViewHolder<?>> holder) {
        DEFAULT_VIEW_HOLDER_FOR_TYPE.put(type.hashCode(), holder);
    }

    /**
     * Update adapter data set, refresh adapter items with {@link DiffUtil}.
     * The ViewHolder must override {@link AbsViewHolder#getChangePayloads(Object, Object)} to make item data
     * comparable, to compare the item data content (NOT object), the item data best override the
     * method <code>equals</code>.
     *
     * @param newData The new data set.
     * @throws IllegalArgumentException Throw when the object <code>newData</code> is equals to
     *                                  the old data set.
     * @see SuperDiffHelper
     */
    public void updateWithDiff(List<?> newData) throws IllegalArgumentException {
        if (mRecyclerView == null) {
            mDataSet.clear();
            mDataSet.addAll(newData);
            return;
        }
        if (newData.equals(mDataSet)) {
            throw new IllegalArgumentException("The object new data set is equals to the old data set.");
        }
        SuperDiffHelper helper = new SuperDiffHelper(this);
        DiffUtil.DiffResult diffResult = helper.update(mDataSet, newData);
        mDataSet.clear();
        mDataSet.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    @Deprecated
    public void setItemData(List<Object> data) {
        mDataSet = data;
    }

    @Deprecated
    public void addItemData(List<Object> data) {
        mDataSet.addAll(data);
    }

    /**
     * Set whether show `No Data` view when data set is empty, when the data changes and is detected
     * to be empty, the emptyData will add to data set and notify adapter rebinding ViewHolder.
     *
     * @param enableEmptyView True expressed enable, otherwise not.
     * @param emptyData       The item data use for `No Data`'s ViewHolder.
     */
    public void setEnableEmptyView(boolean enableEmptyView, @Nullable Object emptyData) {
        mEnableEmptyView = enableEmptyView;
        mEmptyData = emptyData;
    }

    /**
     * Set whether show `No Data` view before first time call adapter's `notifyXxx` method.
     *
     * @param enableEmptyViewOnInit True will show before `notifyChange`.
     */
    public void setEnableEmptyViewOnInit(boolean enableEmptyViewOnInit) {
        mEnableEmptyViewOnInit = enableEmptyViewOnInit;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.mOnItemLongClickListener = longClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * Specify AbsViewHolder class for item data type.
     *
     * @param type   The type of item.
     * @param holder The ViewHolder extends AbsViewHolder for create view and binding data.
     */
    public <T> void addViewHolderForType(Class<T> type, Class<? extends AbsViewHolder<T>> holder) {
        mItemViewHolderForType.put(type.hashCode(), holder);
    }

    public <T> void addViewHolderGenerator(Class<T> type, IViewHolderGenerator<T> generator) {
        mHolderGenerators.put(type.hashCode(), generator);
    }

    /**
     * Add a header or footer item for the list, if your want remove header or footer, pass parameter
     * `generator` a null value.
     *
     * @param header    True expressed set header, otherwise footer.
     * @param data      The data need be bind to item.
     * @param generator The interface use for generate ViewHolder.
     */
    public <T> void setHeaderOrFooter(boolean header, @Nullable T data,
                                      @Nullable IViewHolderGenerator<T> generator) {
        if (generator == null) {
            mHolderGenerators.remove(data.getClass().hashCode());
            if (header) {
                mHeader = null;
            } else {
                mFooter = null;
            }
            return;
        }
        mHolderGenerators.put(data.getClass().hashCode(), generator);
        if (header) {
            mHeader = data;
        } else {
            mFooter = data;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AbsViewHolder<?> viewHolder = null;
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
        bindViewHolder(viewHolder, position, null);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        bindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        int size = mDataSet.size();
        if (mHeader != null) {
            size++;
        }
        if (mFooter != null) {
            size++;
        }
        return size;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof AbsViewHolder) {
            ((AbsViewHolder<?>) holder).onRecycled();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int offset = 0;
        if (mHeader != null) {
            offset++;
            if (position == 0) {
                return mHeader.getClass().hashCode();
            }
        }
        if (mFooter != null) {
            boolean isFooter = position == offset + mDataSet.size();
            if (isFooter) {
                return mFooter.getClass().hashCode();
            }
        }
        return mDataSet.get(position - offset).getClass().hashCode();
    }

    @Override
    public long getItemId(int position) {
        if (hasStableIds()) {
            Object itemData = mDataSet.get(position);
            RecyclerView.ViewHolder vh = getViewHolderFor(itemData);
            if (vh instanceof AbsViewHolder) {
                return ((AbsViewHolder<?>) vh).getItemDataIdInternal(position, itemData);
            }
        }
        return super.getItemId(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mFirstTimeLoadData = true;
        mRecyclerView = recyclerView;
        if (mDataObserver == null) {
            mDataObserver = new DataObserver();
        }
        registerAdapterDataObserver(mDataObserver);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mRecyclerViewTreeObserver);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mFirstTimeLoadData = true;
        if (mDataObserver != null) {
            unregisterAdapterDataObserver(mDataObserver);
        }
        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(mRecyclerViewTreeObserver);
        mRecyclerView = null;
    }

    @Nullable
    RecyclerView.ViewHolder getViewHolderFor(Object object) {
        return mRecyclerView.getRecycledViewPool().getRecycledView(object.hashCode());
    }

    private void bindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position,
                                @Nullable List<Object> payloads) {
        if (viewHolder instanceof AbsViewHolder) {
            AbsViewHolder<?> absViewHolder = ((AbsViewHolder<?>) viewHolder);
            Object data = getItemData(position);
            absViewHolder.setOnClickListener(mItemClickListener);
            absViewHolder.setOnLongClickListener(mItemLongClickListener);
            absViewHolder.onBindViewHolder(this, mDataSet, data, position, payloads);
        }
    }

    private Object getItemData(int position) {
        Object data;
        int itemType = getItemViewType(position);
        if (mHeader != null && position == 0) {
            data = mHeader;
        } else if (mFooter != null && itemType == mFooter.getClass().hashCode()) {
            data = mFooter;
        } else {
            int offset = mHeader != null ? 1 : 0;
            data = mDataSet.get(position - offset);
        }
        return data;
    }

    private void updateEmptyView() {
        if (mDataSet.isEmpty()) {
            mDataSet.add(mEmptyData);
            super.notifyDataSetChanged();
        }
        if (!mDataSet.isEmpty() && mDataSet.size() > 1) {
            int indexOfEmpty = mDataSet.indexOf(mEmptyData);
            if (indexOfEmpty >= 0) {
                mDataSet.remove(indexOfEmpty);
                super.notifyDataSetChanged();
            }
        }
    }

    private AbsViewHolder<?> getHolder(Class<? extends AbsViewHolder<?>> clazz,
                                       ViewGroup parent, int type) {
        Object result = null;
        try {
            for (Constructor<?> c : clazz.getDeclaredConstructors()) {
                c.setAccessible(true);
                result = getViewHolder(c, parent);
                if (result != null) {
                    break;
                }
            }
            if (result == null) {
                throw new CreateViewHolderException(
                        "No suitable constructor find with ViewHolder " + clazz.getName()
                );
            }
        } catch (IllegalAccessException e) {
            throw new CreateViewHolderException(e);
        } catch (InvocationTargetException e) {
            throw new CreateViewHolderException(e);
        } catch (InstantiationException e) {
            throw new CreateViewHolderException(e);
        }
        return (AbsViewHolder<?>) result;
    }

    private Object getViewHolder(Constructor<?> c, View parent)
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
        void onItemClick(View source, Object itemData, int position, Object other);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View source, Object itemData, int position);
    }

    public interface IViewHolderGenerator<T> {
        @NonNull
        AbsViewHolder<T> onCreateViewHolder(ViewGroup parent);
    }

    // Expressed the empty item data.
    public static final class Empty {
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mFirstTimeLoadData = false;
        }
    }
}
