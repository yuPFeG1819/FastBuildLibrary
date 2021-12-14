package com.yupfeg.base.widget.recyclerview.itemdecoration.config

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat


/**
 * 基础列表item辅助线（间距）的配置类
 * @author yuPFeG
 * @date 2021/02/25
 */
data class SimpleItemDividerConfig(
    /**
     * item左侧辅助线（间距）尺寸，单位dp
     * * 在itemView左侧留出的偏移量，留出绘制辅助线位置
     * */
    var left : Float = 0f,
    /**
     * item顶部辅助线（间距）尺寸，单位dp
     * * 在itemView上方留出的偏移量，留出绘制辅助线位置
     * */
    var top : Float = 0f,
    /**
     * item右侧辅助线（间距）尺寸，单位dp
     * * 在itemView右侧留出的偏移量，留出绘制辅助线位置
     * */
    var right : Float = 0f,
    /**
     * item底部（间距）尺寸，单位dp
     * * 同时在itemView下方留出的偏移量，留出绘制辅助线位置
     * */
    var bottom : Float = 0f,
    /**
     * 分割线（间距）的绘制颜色id
     * * 默认为[Color.TRANSPARENT],不进行绘制，只留出item间的[horizontalSize]与[verticalSize]位置
     * * 推荐通过[ContextCompat.getColor]或者[Color.parseColor]获取
     * */
    @ColorInt var colorInt: Int = Color.TRANSPARENT,

    /**是否排除最后一项item，默认为true，不对最后一项item绘制*/
    var excludeLastItem : Boolean = true
)
