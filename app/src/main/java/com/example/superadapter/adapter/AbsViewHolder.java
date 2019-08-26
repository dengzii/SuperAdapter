package com.example.superadapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
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
 * </pre>
 */
@SuppressWarnings({"WeakerAccess", "unchecked", "unused"})
public abstract class AbsViewHolder<T> extends RecyclerView.ViewHolder {

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private T mData;
    private SuperAdapter mAdapter;
    private List<Object> mDataSet;
    private ViewGroup mParent;

    /**
     * 默认 item 会创建并添加一个空白布局, 这个构造器必须被调用, 且必须为 public
     *
     * @param parent The item parent
     */
    public AbsViewHolder(@NonNull ViewGroup parent) {
        this(parent, RecyclerView.VERTICAL);
    }

    public AbsViewHolder(@NonNull ViewGroup parent, @RecyclerView.Orientation int orientation) {
        super(getContainer(parent, orientation));
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
        if (orientation == LinearLayout.VERTICAL) {
            return getVerticalAdaptContainer(parent);
        } else {
            return getHorizontalAdaptContainer(parent);
        }
    }

    private static ViewGroup getVerticalAdaptContainer(View parent) {

        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return frameLayout;
    }

    private static ViewGroup getHorizontalAdaptContainer(View parent) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
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

    protected void setContentView(View content) {
        addContent(content);
    }

    protected SuperAdapter getAdapter() {
        return mAdapter;
    }

    protected List<Object> getDataSet() {
        return mDataSet;
    }

    protected Context getContext() {
        return mParent.getContext();
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
    void onBindViewHolder(SuperAdapter adapter, List<Object> dataSet, Object data, int position) {

        mDataSet = dataSet;
        mAdapter = adapter;
        ItemInfo mItemInfo = new ItemInfo(null, -1, this);
        mItemInfo.data = data;
        mItemInfo.position = getAdapterPosition();
        itemView.setTag(mItemInfo);
        itemView.setClickable(true);
        itemView.setLongClickable(true);
        itemView.setOnClickListener(mOnClickListener);
        itemView.setOnLongClickListener(mOnLongClickListener);
        onBindData((T) data, position);
    }

    private void addContent(View view) {
        if (view != null && itemView instanceof ViewGroup) {
            ((ViewGroup) itemView).addView(view);
        }
    }

    /**
     * 点击事件时当前 item 数据容器类
     */
    static class ItemInfo {
        Object data;
        int position;
        AbsViewHolder absViewHolder;

        ItemInfo(Object data, int position, AbsViewHolder absViewHolder) {
            this.data = data;
            this.position = position;
            this.absViewHolder = absViewHolder;
        }
    }
}
