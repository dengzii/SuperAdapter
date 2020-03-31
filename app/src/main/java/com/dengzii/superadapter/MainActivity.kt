package com.dengzii.superadapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dengzii.adapter.AbsViewHolder
import com.dengzii.adapter.SuperAdapter
import com.example.superadapter.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = listOf(
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
        // 绑定数据类到 ViewHolder
        adapter.addViewHolderForType(Item::class.java, ItemViewHolder::class.java)
        adapter.addViewHolderForType(Header::class.java, HeaderViewHolder::class.java)
        adapter.addViewHolderForType(Section::class.java, SectionViewHolder::class.java)

        adapter.setOnItemClickListener(object : SuperAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, itemData: Any?, position: Int) {

            }
        })

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

    class Header(
        var title: String,
        var content: String
    )

    class Item(
        var title: String,
        var content: String,
        var img: Int
    )

    class Section(
        var title: String
    )
}