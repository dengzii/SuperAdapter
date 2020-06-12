package com.dengzii.superadapter

import android.view.ViewGroup
import android.widget.Button
import com.dengzii.adapter.AbsViewHolder
import com.example.superadapter.R

/**
 * author : dengzi
 * e-mail : master@dengzii.com
 * time   : 20-6-9
 * desc   : none
 *
 */
class EmptyViewHolder(parent: ViewGroup) : AbsViewHolder<Any>(
    parent, LAYOUT_MATCH_PARENT_VH
) {
    private val mBtRefresh by lazy { findViewById<Button>(R.id.bt_refresh) }

    override fun onCreate(parent: ViewGroup) {
        setContentView(R.layout.item_empty)
    }

    override fun onBindData(data: Any, position: Int) {
        bindViewClick(mBtRefresh)
    }
}