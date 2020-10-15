package com.dengzii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * time   : 2019/07/09 10:11
 * desc   : abstract view holder for multi-type recycler view
 *
 * 继承这个类时, 类必须为 public, 当子类为内部类时, 必须为 static, 否则 Adapter 无法实例化
 * 且必须调用父类的默认构造器, 默认该 ViewHolder 带一个空的宽度为 match_parent 的 FrameLayout
 *
 * usage  :
 *
 * <code>
 *  public class MyViewHolder extends AbsViewHolder{
 *      public MyViewHolder(@NonNull ViewGroup parent){
 *          super(parent);
 *          // setContentView(R.layout.item_user_data);
 *      }
 *      public void onCreate(@NonNull ViewGroup parent) {
 *          // setContentView(R.layout.item_user_data);
 *      }
 *      public void onBindData(Object data, int position) {
 *
 *      }
 *  }
 *
 * </code>
 *
 * </pre>
 */
@SuppressWarnings({"WeakerAccess", "unchecked", "unused"})
public abstract class AbsViewHolder<T> extends RecyclerView.ViewHolder {

    public static final FrameLayout.LayoutParams LAYOUT_MATCH_PARENT_VH = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private T mData;
    private SuperAdapter mAdapter;
    private List<Object> mDataSet;
    private ViewGroup mParent;

    public Class<?> itemClazz;

    /**
     * 默认 item 会创建并添加一个空白布局, 这个构造器必须被调用, 且必须为 public
     *
     * @param parent The item parent
     */
    public AbsViewHolder(@NonNull ViewGroup parent) {
        this(parent, LinearLayoutManager.VERTICAL);
    }

    public AbsViewHolder(@NonNull ViewGroup parent, @RecyclerView.Orientation int orientation) {
        super(getContainer(parent, orientation));
        this.mParent = parent;
        onCreate((ViewGroup) itemView);
    }

    public AbsViewHolder(@NonNull ViewGroup parent, FrameLayout.LayoutParams layoutParams) {
        super(getAdaptContainer(parent, layoutParams));
        this.mParent = parent;
        onCreate((ViewGroup) itemView);
    }

    /**
     * 生成一个给定方向尺寸固定的空白 FrameLayout
     *
     * @param parent      The item parent
     * @param orientation The direction which need be fixation size
     * @return The blank FrameLayout container
     */
    protected static ViewGroup getContainer(View parent, @RecyclerView.Orientation int orientation) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        if (orientation == LinearLayout.VERTICAL) {
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        }
        frameLayout.setLayoutParams(layoutParams);
        return frameLayout;
    }

    /**
     * 当 Adapter 调用 onCreateViewHolder 时 创建该 item 的时候调用这个方法
     * 这个时候, 这个 item 的容器布局已经创建了
     *
     * @param parent The container, parent of this item
     */
    public abstract void onCreate(@NonNull ViewGroup parent);

    /**
     * 当 Adapter 调用 onBindingViewHolder 绑定数据时调用这个方法
     *
     * @param data     The data of item
     * @param position The position of item
     */
    public abstract void onBindData(@NonNull T data, int position);

    public void onBindData(@NonNull T data, int position, List<Object> payloads) {

    }

    /**
     * 从当前 item 中查找指定 id 的 View
     *
     * @param id  The id of the View need be find
     * @param <Z> The type of the View
     * @return The View of the specified id
     */
    protected <Z extends View> Z findViewById(@IdRes int id) {
        return itemView.findViewById(id);
    }

    /**
     * 设置 item 的布局
     * itemView 默认带一个 空的 FrameLayout, 通过这个方法设置的布局将自动添加到 itemView 中
     *
     * @param res The id of layout res
     */
    protected void setContentView(@LayoutRes int res) {
        if (itemView instanceof ViewGroup) {
            addContent(LayoutInflater
                    .from(itemView.getContext())
                    .inflate(res, null, false));
        }
    }

    protected void setLayoutParam(FrameLayout.LayoutParams layoutParam) {
        if (itemView instanceof FrameLayout) {
            itemView.setLayoutParams(layoutParam);
        }
    }

    protected void setContentView(View content) {
        addContent(content);
    }

    protected SuperAdapter getAdapter() {
        return mAdapter;
    }

    protected List<Object> getDataSet() {
        return mDataSet;
    }

    @CallSuper
    protected void onRecycled() {

    }

    @Nullable
    protected Object diff(Object other) {
        return null;
    }

    protected Context getContext() {
        return mParent.getContext();
    }

    /**
     * 为 View 绑定 onClick 事件
     * 事件将在 SuperAdapter#setOnItemClickListener 中回调
     *
     * @param view 需要绑定 onClick 事件的 View
     * @see AbsViewHolder#onViewClick(View, Object)
     */
    protected void bindViewClick(View view) {
        if (mOnClickListener != null) {
            view.setTag(getItemInfo(view, null));
            view.setOnClickListener(mOnClickListener);
        }
    }

    /**
     * 当 Item 布局中 View 需要传递 onClick 事件到外部(eg. Activity, Fragment)时,
     * 需要为该 View 设置点击事件并在设置的点击事件内调用此方法
     * 事件将在 SuperAdapter#setOnItemClickListener 中回调
     *
     * @param view  Item 中被点击的 view, 该view 会被传递到 activity 以识别是哪个 view 被点击了
     * @param other 附加对象,
     */
    protected void onViewClick(View view, Object other) {
        if (mOnClickListener != null) {
            view.setTag(getItemInfo(view, other));
            mOnClickListener.onClick(view);
        }
    }

    /**
     * @param view  Item 中被点击的 View
     * @param other 附加对象
     * @see AbsViewHolder#onViewClick(View, Object)
     */
    protected void onLongClick(View view, Object other) {
        if (mOnLongClickListener != null) {
            view.setTag(getItemInfo(view, other));
            view.setClickable(true);
            view.setLongClickable(true);
            mOnLongClickListener.onLongClick(view);
        }
    }

    /**
     * 设置该 item 的点击事件
     *
     * @param onClickListener The OnClickListener
     */
    void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
    }

    /**
     * 绑定数据到该 item, 设置点击事件
     *
     * @param data     The data of item
     * @param position The position of item
     */
    void onBindViewHolder(SuperAdapter adapter, List<Object> dataSet, Object data, int position,
                          @Nullable List<Object> payloads) {

        mData = (T) data;
        mDataSet = dataSet;
        mAdapter = adapter;
        if (payloads != null) {
            onBindData((T) data, position, payloads);
        } else {
            onBindData((T) data, position);
        }
    }

    private ItemInfo getItemInfo(View source, Object other) {
        return new ItemInfo(mData, getAdapterPosition(), this, source, other);
    }

    private void addContent(View view) {
        if (view != null && itemView instanceof ViewGroup) {
            ((ViewGroup) itemView).addView(view);
        }
    }

    private static ViewGroup getAdaptContainer(View parent, FrameLayout.LayoutParams layoutParams) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(layoutParams);
        return frameLayout;
    }

    /**
     * 点击事件时当前 item 数据容器类, 里面包含了该当前 item 的一些基本信息,
     * 比如触发事件的 View source, position, 当前 item 的数据对象
     */
    static class ItemInfo {
        Object data;
        int position;
        AbsViewHolder<?> absViewHolder;
        View source;
        Object other;

        ItemInfo(Object data, int position, AbsViewHolder<?> absViewHolder, View source, Object other) {
            this.data = data;
            this.position = position;
            this.absViewHolder = absViewHolder;
            this.source = source;
            this.other = other;
        }
    }
}
