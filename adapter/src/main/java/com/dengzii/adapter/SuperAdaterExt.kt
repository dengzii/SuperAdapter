package com.dengzii.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes


/**
 * The AbsViewHolder DSL builder.
 * @author dengzi
 */
class AbsViewHolderBuilder<T>(
    private val viewHolder: AbsViewHolder<T>
) {

    val itemView: View get() = viewHolder.itemView
    val layoutPosition: Int get() = viewHolder.layoutPosition
    val adapterPosition: Int get() = viewHolder.adapterPosition
    var isRecyclable: Boolean
        get() = viewHolder.isRecyclable
        set(value) {
            viewHolder.setIsRecyclable(value)
        }

    var onBindData: ((data: T, position: Int) -> Unit)? = null
        private set

    /**
     * Bind data.
     * @param action The bind data action scope function.
     */
    fun onBindData(action: (data: T, position: Int) -> Unit) {
        this.onBindData = action
    }

    /**
     * FindViewById from item view.
     * @param id The view id.
     */
    fun <V : View> findView(@IdRes id: Int): V {
        return viewHolder.findViewById<V>(id)
    }

    /**
     * FindViewById lazy load mode.
     * @param id The view id.
     */
    fun <V : View> lazyFindView(@IdRes id: Int): Lazy<V> {
        return lazy(LazyThreadSafetyMode.NONE) {
            viewHolder.findViewById<V>(id)
        }
    }
}

/**
 * Remove header item.
 * @param data The header data.
 */
inline fun <reified T> SuperAdapter.removeHeader(data: T?) {
    setHeaderOrFooter(true, data, null)
    notifyItemRemoved(0)
}

/**
 * Remove footer item.
 *
 */
inline fun <reified T> SuperAdapter.removeFooter(data: T?) {
    setHeaderOrFooter(false, data, null)
    notifyItemRemoved(itemCount - 1)
}

/**
 * Add footer item.
 * @param data The item data for footer view holder.
 * @param layoutRes The layout res for footer.
 * @param action The footer view bind action scope function.
 */
inline fun <reified T> SuperAdapter.setFooter(
    data: T,
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    setHeaderOrFooter(false, data, layoutRes, action)
    notifyItemInserted(itemCount - 1)
}

/**
 * Add header item.
 * @param data The item data for header view holder.
 * @param layoutRes The layout res for header.
 * @param action The header view bind action scope function.
 */
inline fun <reified T> SuperAdapter.setHeader(
    data: T,
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    setHeaderOrFooter(true, data, layoutRes, action)
    notifyItemInserted(0)
}

/**
 * Add header or footer item for adapter.
 * @param header True expressed the header, otherwise footer.
 * @param data The item data bind to header or footer.
 * @param layoutRes The layout resource for item.
 * @param action The view bind action scope function.
 */
inline fun <reified T> SuperAdapter.setHeaderOrFooter(
    header: Boolean,
    data: T,
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    this.setHeaderOrFooter(header, data) {
        buildAbsViewHolder(it, layoutRes, action)
    }
}

/**
 * Add view holder for specify type [T].
 * @param T The type of item data.
 * @param layoutRes The layout resource for item type.
 * @param action The view bind action scope function.
 */
inline fun <reified T> SuperAdapter.addViewHolderForType(
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    this.addViewHolderGenerator(T::class.java) {
        buildAbsViewHolder(it, layoutRes, action)
    }
}

/**
 * Build the [AbsViewHolder] with given param.
 */
inline fun <reified T> buildAbsViewHolder(
    parent: ViewGroup,
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
): AbsViewHolder<T> {
    return object : AbsViewHolder<T>(parent) {
        lateinit var builder: AbsViewHolderBuilder<T>
        override fun onCreate(parent: ViewGroup) {
            builder = AbsViewHolderBuilder<T>(this)
            layoutRes?.apply {
                setContentView(this)
                return
            }
            action(builder, this)
        }

        override fun onBindData(data: T, position: Int) {
            builder.onBindData?.invoke(data, position)
        }
    }
}