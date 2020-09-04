## SuperAdapter
[![#](https://badgen.net/badge/icon/awesome?icon=awesome&label)](#)
[![download](https://api.bintray.com/packages/dengzi/maven/superadapter/images/download.svg) ](https://bintray.com/dengzi/maven/superadapter/_latestVersion) 
[![#](https://badgen.net/github/release/dengzii/superadapter)](#)
[![#](https://badgen.net/github/last-commit/dengzii/superadapter)](#)
[![#](https://badgen.net/github/license/dengzii/superadapter)](#)

简单, 快捷, 好用的 RecyclerView 适配器.

**功能**

- 无需继承 Adapter, 无需通过 position 判断 item 类型.
- 支持页头和页脚.
- 支持自动展示空数据界面.
- 通过 Kotlin 的 lambda 大量缩减代码.

## Usage

添加依赖 
[![download](https://api.bintray.com/packages/dengzi/maven/superadapter/images/download.svg) ]

```
implementation "com.dengzii.adapter:$latestVersion"
```

通过 lambda 快速使用

```kotlin
adapter.setEnableEmptyView(true, SuperAdapter.Empty())
adapter.addViewHolderForType<SuperAdapter.Empty>(R.layout.item_empty){
    onBindData { _, _ -> 
        findView<View>(R.id.bt_refresh).setOnClickListener { 
            // refresh your data
        }
    }
}
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


或者不使用 lambda

```kotlin
val adapter = SuperAdapter(listOf("Item 1", "Item 2", "Item 3"))
adapter.addViewHolderForType(String::class.java, ItemViewHolder::class.java)
recyclerView.layoutManager = LinearLayoutManager(this)
recyclerView.adapter = adapter

class ItemViewHolder(parent: ViewGroup) : AbsViewHolder<String>(parent) {
    private lateinit var mTextView:TextView 
    override fun onCreate(parent: ViewGroup) {
        mTextView = TextView(context)
        mTextView.layoutParams = getLayoutParam(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        setContentView(mTextView)
    }

    override fun onBindData(data: String, position: Int) {
        mTextView.text = data
    }
}
```
无需继承 SuperAdapter, 但需要为每种 Item 实现并继承继承抽象类 AbsViewHolder<T>, 并在改类中设置布局和绑定 View, 数据.

AbsViewHolder<T> 代表一种 Item 类型, 其中泛型 T 为该 Item 对应的实体类

## SuperAdapter

**Adapter 构造器**

    public SuperAdapter(List<Object> data)

**绑定 ViewHolder 到实体类型**
    
    public void addViewHolderForType(Class<?> type, Class<? extends AbsViewHolder> holder)

**设置 Item 点击事件**
    
     public void setOnItemClickListener(OnItemClickListener listener)
     
     public interface OnItemClickListener{
         void onItemClick(View v, Object itemData, int position);
     }

## AbsViewHolder

**构造器, 必须重写带参数构造器, 否则无法使用, parent 与 Adapter#onCreateViewHolder 中 parent 一致**

     public AbsViewHolder(@NonNull ViewGroup parent) 

**创建 Item 时调用 onCreate 方法, 其中 parent 是该 item 的容器布局**
    
     public abstract void onCreate(@NonNull ViewGroup parent);

**绑定数据**

    public abstract void onBindData(@NonNull T data, int position);     

## Sample

**设置 Adapter**

    var data:List<Any>
    ...
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

**继承 AbsViewHolder**

    class ItemViewHolder(parent: ViewGroup) : AbsViewHolder<Item>(parent) {
        private lateinit var mTvTitle: TextView
        private lateinit var mTvContent: TextView
        private lateinit var mIvImage:ImageView
    
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

## Screenshot

![screenshot](https://github.com/MrDenua/SuperAdapter/blob/master/screenshot/screenshot.png?raw=true)
