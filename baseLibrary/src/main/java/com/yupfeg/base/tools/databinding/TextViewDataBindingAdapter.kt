package com.yupfeg.base.tools.databinding

import android.text.method.MovementMethod
import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.yupfeg.base.widget.ext.DSLTextWatcher

/**
 * [TextView]的拓展函数，设置文本加粗
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param isBold 是否加粗
 */
@Suppress("unused")
@BindingAdapter("setBoldText")
fun TextView.bindBoldText(isBold : Boolean){
    this.paint.isFakeBoldText = isBold
}

/**
 * [TextView]的拓展函数，设置文本加粗
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param textSize 文本大小 sp
 */
@Suppress("unused")
@BindingAdapter("setTextSize")
fun TextView.bindTextSize(textSize : Int){
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize.toFloat())
}

/**
 * [TextView]的拓展函数，设置TextView的movementMethod
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * */
@Suppress("unused")
@BindingAdapter(value = ["movementMethod"])
fun TextView.bindMovementMethod(movement : MovementMethod?){
    movement?:return
    movementMethod = movement
}

/**
 * [TextView]的拓展函数，设置TextView的高亮颜色
 * * 通常为配合富文本的点击，设置为透明色，防止点击出现高亮颜色背景
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * */
@Suppress("unused")
@BindingAdapter("highLightColor")
fun TextView.bindHighLightColor(@ColorInt color : Int?){
    color?:return
    highlightColor = color
}

/**
 * [EditText]的拓展函数，设置文本输入监听，以dsl方式简化文本输入监听
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param textInputWatcher 文本输入监听
 */
@Suppress("unused")
@BindingAdapter("setDslTextWatch")
fun EditText.bindingDslTextWatch(textInputWatcher : DSLTextWatcher){
    this.addTextChangedListener(textInputWatcher)
}