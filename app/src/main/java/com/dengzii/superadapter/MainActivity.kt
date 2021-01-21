package com.dengzii.superadapter

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dengzii.adapter.*
import com.example.superadapter.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val data = mutableListOf<Any>()
    private val adapter = SuperAdapter(data)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SuperAdapter.addDefaultViewHolderForType(
            SuperAdapter.EMPTY::class.java,
            EmptyViewHolder::class.java
        )

        data.addAll(
            mutableListOf(
                Section("Section 0"),
                Item("Item 1", "content content content 1", R.mipmap.ic_launcher),
                Item("Item 2", "content content content 2", R.mipmap.ic_launcher_round),
                Item("Item 3", "content content content 3", R.mipmap.ic_launcher),
                Item("Item 4", "content content content 4", R.mipmap.ic_launcher_round),
                "This item is no view holder",
                Section("Section 1"),
                Item("Item 5", "content content content 5", R.mipmap.ic_launcher_round)
            )
        )

        adapter.setEnableEmptyViewOnInit(true)
        adapter.setEnableEmptyView(true, SuperAdapter.EMPTY)
        // 绑定数据类到 ViewHolder
        ktx()
//        bindViewHolder()

        adapter.setOnItemClickListener { _, itemData, _, _ ->
            // do something
            if (itemData == SuperAdapter.EMPTY) {
                data.add(0, Item("Item", "content content content", R.mipmap.ic_launcher))
                adapter.notifyDataSetChanged()
                return@setOnItemClickListener
            }
            Toast.makeText(this, itemData.toString(), Toast.LENGTH_SHORT).show()
        }

        button.setOnClickListener {
            data.clear()
            adapter.notifyDataSetChanged()
        }
        bt_add.setOnClickListener {
            data.add(Item("Item added", "content content content added", R.mipmap.ic_launcher))
            adapter.notifyDataSetChanged()
        }
        bt_update.setOnClickListener {
            val nData = data.toMutableList()
            nData.removeAt(0)
            nData.add(0, Item("Item 0", "item updated", R.mipmap.ic_launcher))
            nData.removeAt(3)
            adapter.updateWithDiff(nData)
        }
        val recyclerView: RecyclerView = findViewById(R.id.rv_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun ktx() {

        adapter.setHeader("This is header", R.layout.item_header) {
            onBindData { data, _ ->
                findView<TextView>(R.id.tv_title).text = data
            }
        }
        adapter.setFooter(listOf("This", "is", "footer"), R.layout.item_section) {
            onBindData { data, _ ->
                findView<TextView>(R.id.tv_title).text = data.joinToString(" ")
            }
        }
        adapter.addViewHolderForType<Header>(R.layout.item_header) {
            val title = findView<TextView>(R.id.tv_title)
            val content by lazyFindView<TextView>(R.id.tv_content)
            onBindData { data, _ ->
                title.text = data.title
                content.text = data.content
            }
        }
        adapter.addViewHolderForType<Item>(R.layout.item_item) { v ->
            val title = findView<TextView>(R.id.tv_title)
            val content = findView<TextView>(R.id.tv_content)
            val icon = findView<ImageView>(R.id.iv_img)
            onBindData { data, _ ->
                title.text = data.title
                content.text = data.content
                icon.setImageResource(data.img)
                v.itemView.setOnClickListener {
                    // do something
                }
            }
        }
        adapter.addViewHolderForType<Section>(R.layout.item_section) {
            onBindData { data, _ ->
                findView<TextView>(R.id.tv_title).text = data.title
            }
        }
    }

    private fun bindViewHolder() {
        adapter.addViewHolderForType(Item::class.java, ItemViewHolder::class.java)
    }

    /**
     *
     */
    class ItemViewHolder(parent: ViewGroup) : AbsViewHolder<Item>(parent) {

        val title: TextView by lazy { findViewById<TextView>(R.id.tv_title) }
        val content: TextView by lazy { findViewById<TextView>(R.id.tv_content) }
        val icon: ImageView by lazy { findViewById<ImageView>(R.id.iv_img) }

        override fun onCreate(parent: ViewGroup) {
            setContentView(R.layout.item_item)
        }

        override fun onBindData(data: Item, position: Int) {
            title.text = data.title
            content.text = data.content
            icon.setImageResource(data.img)
        }

//        override fun getItemDataId(position: Int, data: Item?): Long {
//            return data?.hashCode()?.toLong() ?: -1L
//        }
//
//        override fun diff(old: Any, new_: Any): Any? {
//            return new_
//        }
    }

    /**
     *
     */
    data class Header(
        var title: String,
        var content: String
    )

    /**
     *
     */
    data class Item(
        var title: String,
        var content: String,
        var img: Int
    ) {
//        override fun equals(other: Any?): Boolean {
//            return other.hashCode() == this.hashCode()
//        }
    }

    /**
     *
     */
    data class Section(
        var title: String
    )
}