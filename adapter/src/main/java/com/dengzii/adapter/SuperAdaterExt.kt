package com.dengzii.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes

class AbsViewHolderBuilder<T>(
    private val viewHolder: AbsViewHolder<T>
) {

    var view: View? = null

    var onBindData: ((data: T, position: Int) -> Unit)? = null
        private set

    fun onBindData(action: (data: T, position: Int) -> Unit) {
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

inline fun <reified T> SuperAdapter.addViewHolderForType(
    @IdRes layoutRes: Int? = null,
    crossinline action: AbsViewHolderBuilder<T>.(vh: AbsViewHolder<T>) -> Unit
) {
    this.addViewHolderGenerator(T::class.java) {
        object : AbsViewHolder<T>(it) {
            var builder = AbsViewHolderBuilder<T>(this)
            override fun onCreate(parent: ViewGroup) {
                layoutRes?.apply {
                    setContentView(this)
                    return
                }
                action(builder, this)
                builder.view?.apply {
                    setContentView(this)
                    return
                }
            }

            override fun onBindData(data: T, position: Int) {
                builder.onBindData?.invoke(data, position)
            }
        }
    }
}
