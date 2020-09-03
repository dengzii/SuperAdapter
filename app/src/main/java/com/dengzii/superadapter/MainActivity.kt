package com.dengzii.superadapter

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dengzii.adapter.SuperAdapter
import com.dengzii.adapter.addViewHolderForType
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

//        data.addAll(
//            mutableListOf(
//                Header("Title", "subtitle subtitle."),
//                Section("Section 0"),
//                Item("Item 1", "content content content 1", R.mipmap.ic_launcher),
//                Item("Item 2", "content content content 2", R.mipmap.ic_launcher_round),
//                Item("Item 3", "content content content 3", R.mipmap.ic_launcher),
//                Item("Item 4", "content content content 4", R.mipmap.ic_launcher_round),
//                "This item is no view holder",
//                Section("Section 1"),
//                Item("Item 5", "content content content 5", R.mipmap.ic_launcher_round)
//            )
//        )

        adapter.setEnableEmptyViewOnInit(false)
        adapter.setEnableEmptyView(true)
        // 绑定数据类到 ViewHolder
        ktx()

        adapter.setOnItemClickListener { _, itemData, _, _ ->
            // do something
            if (itemData == SuperAdapter.EMPTY) {
                data.add(0, Item("Item 1", "content content content 1", R.mipmap.ic_launcher))
                adapter.notifyItemInserted(0)
                return@setOnItemClickListener
            }
            Toast.makeText(this, itemData.toString(), Toast.LENGTH_SHORT).show()
        }

        button.setOnClickListener {
            data.clear()
            adapter.notifyDataSetChanged()
        }
        bt_add.setOnClickListener {
            data.add(Item("Item 1", "content content content 1", R.mipmap.ic_launcher))
            adapter.notifyDataSetChanged()
        }

        val recyclerView: RecyclerView = findViewById(R.id.rv_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun ktx() {

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
    data class Header(
        var title: String,
        var content: String
    )

    data class Item(
        var title: String,
        var content: String,
        var img: Int
    )

    data class Section(
        var title: String
    )
}