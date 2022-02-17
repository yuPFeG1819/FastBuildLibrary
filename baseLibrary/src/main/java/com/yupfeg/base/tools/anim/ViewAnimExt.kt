package com.yupfeg.base.tools.anim

import android.animation.ObjectAnimator
import android.view.View

/**
 * [View]拓展函数，构建`float`类型的属性动画
 * @param propertyName 动画修改属性名称
 * @param from 属性起始值
 * @param to 属性结束值
 * @param config 动画配置函数，默认为null
 */
fun View.floatObjectAnim(
    propertyName: String,
    from : Float,to : Float,
    config : (ObjectAnimator.()->Unit)? = null
) : ObjectAnimator{
    return ObjectAnimator.ofFloat(this,propertyName,from,to).apply { config?.invoke(this) }
}

/**
 * [View]拓展函数，构建`float`类型的属性动画
 * @param propertyName 动画修改属性名称
 * @param values 动画属性修改数组，数组最少需要两个元素
 * @param config 动画配置函数，默认为null
 * */
fun View.floatObjectAnim(
    propertyName : String, values : FloatArray,
    config : (ObjectAnimator.()->Unit)? = null
) : ObjectAnimator{
    return ObjectAnimator.ofFloat(this,propertyName, *values).apply { config?.invoke(this) }
}


