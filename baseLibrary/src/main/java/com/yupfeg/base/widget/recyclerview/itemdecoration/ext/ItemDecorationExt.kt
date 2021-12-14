package com.yupfeg.base.widget.recyclerview.itemdecoration.ext

import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.recyclerview.itemdecoration.divider.SimpleDividerItemDecoration
import com.yupfeg.base.widget.recyclerview.itemdecoration.config.SimpleItemDividerConfig

/**
 * [RecyclerView]拓展函数，使用DSL方式，添加简单的辅助线（间距）
 * @param init dsl方式的[SimpleItemDividerConfig]配置辅助线显示参数
 */
@Suppress("unused")
fun RecyclerView.addDslSimpleDivider(init : SimpleItemDividerConfig.()->Unit){
    val config = SimpleItemDividerConfig().apply(init)
    addItemDecoration(SimpleDividerItemDecoration.create(config))
}