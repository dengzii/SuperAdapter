package com.dengzii.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes

class AbsViewHolderBuilder<T>(
    private val viewHolder: AbsViewHolder<T>
) {

    var onBindData: ((data: T, position: Int) -> Unit)? = null
        private set

    fun onBindData(action: (data: T, position: Int) -> Unit) {
        println("-----")
        this.onBindData = action
    }

    fun <V : View> findView(@IdRes id: Int): V {
        return viewHolder.findViewById<V>(id)
    }

    fun <V : View> lazyFindView(@IdRes id: Int): Lazy<V> {
        return lazy(LazyThreadSafetyMode.NONE) {
            viewHolder.findViewById<V>(id)
        }
    }
}

inline fun <reified T> SuperAdapter.removeHeader(data: T) {
    setHeaderOrFooter(true, data, null)
}

inline fun <reified T> SuperAdapter.removeFooter(data: T) {
    setHeaderOrFooter(false, data, null)
}

inline fun <reified T> SuperAdapter.setFooter(
    data: T,
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    setHeaderOrFooter(false, data, layoutRes, action)
}

inline fun <reified T> SuperAdapter.setHeader(
    data: T,
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    setHeaderOrFooter(true, data, layoutRes, action)
}

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

inline fun <reified T> SuperAdapter.addViewHolderForType(
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    this.addViewHolderGenerator(T::class.java) {
        buildAbsViewHolder(it, layoutRes, action)
    }
}

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
                return@apply
            }
            action(builder, this)
            println("<top>.onCreate")
        }

        override fun onBindData(data: T, position: Int) {
            println("<top>.onBindData")
            builder.onBindData?.invoke(data, position)
        }
    }
}