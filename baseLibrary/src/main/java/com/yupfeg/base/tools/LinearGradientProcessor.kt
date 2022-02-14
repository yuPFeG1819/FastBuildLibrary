package com.yupfeg.base.tools

import android.graphics.Color

/**
 * 线性渐变颜色处理器
 * - fork from [获得线性渐变某点的颜色](https://blog.csdn.net/u010593680/article/details/50987671?spm=1001.2014.3001.5501)
 * @author yuPFeG
 * @date 2022/02/14
 */
class LinearGradientProcessor(
    /**起始颜色*/
    private val startColor : Int,
    /**结束颜色*/
    private val endColor : Int,
){

    /**
     * 根据运行比例获取当前的颜色值
     * @param radio 运行比例
     * @return 颜色int值
     * */
    fun getColorByRadio(radio : Float) : Int{
        val startR: Int = Color.red(startColor)
        val startG: Int = Color.green(startColor)
        val startB: Int = Color.blue(startColor)
        val endR: Int = Color.red(endColor)
        val endG: Int = Color.green(endColor)
        val endB: Int = Color.blue(endColor)

        return Color.argb(
            255,
            (startR + ((endR - startR) * radio + 0.5)).toInt(),
            (startG + ((endG - startG) * radio + 0.5)).toInt(),
            (startB + ((endB - startB) * radio + 0.5)).toInt(),
        )
    }
}