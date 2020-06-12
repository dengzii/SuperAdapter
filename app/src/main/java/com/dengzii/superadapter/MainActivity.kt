package com.dengzii.superadapter

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dengzii.adapter.AbsViewHolder
import com.dengzii.adapter.SuperAdapter
import com.example.superadapter.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SuperAdapter.setDefaultViewHolderForType(
            SuperAdapter.EMPTY::class.java,
            EmptyViewHolder::class.java
        )

        val data = mutableListOf(
            Header("Title", "subtitle subtitle."),
            Section("Section 0"),
            Item("Item 1", "content content content 1", R.mipmap.ic_launcher),
            Item("Item 2", "content content content 2", R.mipmap.ic_launcher_round),
            Item("Item 3", "content content content 3", R.mipmap.ic_launcher),
            Item("Item 4", "content content content 4", R.mipmap.ic_launcher_round),
            Section("Section 1"),
            Item("Item 5", "content content content 5", R.mipmap.ic_launcher_round)
        )

        val recyclerView: RecyclerView = findViewById(R.id.rv_list)
        val adapter = SuperAdapter(data)
        adapter.setEnableEmptyView(true)
        // 绑定数据类到 ViewHolder
        adapter.addViewHolderForType(Item::class.java, ItemViewHolder::class.java)
        adapter.addViewHolderForType(Header::class.java, HeaderViewHolder::class.java)
        adapter.addViewHolderForType(Section::class.java, SectionViewHolder::class.java)

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

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    companion object {

        class HeaderViewHolder(parent: ViewGroup) : AbsViewHolder<Header>(parent) {

            private lateinit var mTvTitle: TextView
            private lateinit var mTvContent: TextView

            override fun onCreate(parent: ViewGroup) {
                setContentView(R.layout.item_header)
                mTvTitle = findViewById(R.id.tv_title)
                mTvContent = findViewById(R.id.tv_content)
            }

            override fun onBindData(data: Header, position: Int) {
                mTvTitle.text = data.title
                mTvContent.text = data.content
            }
        }

        class ItemViewHolder(parent: ViewGroup) : AbsViewHolder<Item>(parent) {
            private lateinit var mTvTitle: TextView
            private lateinit var mTvContent: TextView
            private lateinit var mIvImage: ImageView

            override fun onCreate(parent: ViewGroup) {
                setContentView(R.layout.item_item)
                mTvTitle = findViewById(R.id.tv_title)
                mTvContent = findViewById(R.id.tv_content)
                mIvImage = findViewById(R.id.iv_img)
            }

            override fun onBindData(data: Item, position: Int) {
                mTvTitle.text = data.title
                mTvContent.text = data.content
                mIvImage.setImageResource(data.img)
                itemView.setOnClickListener {
                    onViewClick(it, null)
                }
            }
        }

        class SectionViewHolder(parent: ViewGroup) : AbsViewHolder<Section>(parent) {
            private lateinit var mTvTitle: TextView

            override fun onCreate(parent: ViewGroup) {
                setContentView(R.layout.item_section)
                mTvTitle = findViewById(R.id.tv_title)
            }

            override fun onBindData(data: Section, position: Int) {
                mTvTitle.text = data.title
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