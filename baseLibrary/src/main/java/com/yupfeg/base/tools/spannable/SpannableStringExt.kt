package com.yupfeg.base.tools.spannable

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*

/**
 * [SpannableStringBuilder]的拓展函数，添加文本段落，并应用文本样式配置
 * @param text 应用文本样式的文本内容
 * @param init 以Kotlin-DSL方式，配置文本样式类
 */
@Suppress("unused")
fun SpannableStringBuilder.append(
    text: CharSequence,init : SpannableStringStyleConfig.()->Unit
) : SpannableStringBuilder{
    val config = SpannableStringStyleConfig().apply(init)
    append(text,config)
    return this
}

/**
 * [SpannableStringBuilder]的拓展函数，添加文本段落，并应用文本样式配置
 * @param text 应用文本样式的文本内容
 * @param config 文本样式配置
 * */
@Suppress("unused")
fun SpannableStringBuilder.append(
    text : CharSequence,
    config: SpannableStringStyleConfig
) : SpannableStringBuilder{
    addSpannableText(text,config)
    return this
}

/**
 * [SpannableStringBuilder]的拓展函数，添加应用文本样式的一段文本段落
 * @param text 文本内容
 * @param config
 */
private fun SpannableStringBuilder.addSpannableText(text: CharSequence,config: SpannableStringStyleConfig){
    //确定文本样式的作用范围只在当前文本段落内
    val start = this.length
    append(text)
    val end = this.length
    val flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

    //图片替代文本样式，优先级最高，与其他样式冲突
    config.imageSpan?.also {span->
        setSpan(span, start, end, flag)
        return
    }

    //文本颜色
    if (config.textColor != Color.TRANSPARENT){
        setSpan(ForegroundColorSpan(config.textColor), start, end, flag)
    }
    //背景色
    if (config.backgroundColor != Color.TRANSPARENT){
        setSpan(BackgroundColorSpan(config.backgroundColor), start, end, flag)
    }
    //引用线颜色
    if (config.quoteLineColor != Color.TRANSPARENT){
        setSpan(QuoteSpan(config.quoteLineColor), start, end, 0)
    }

    config.textRelativeSizeSpan?.also {span->
        //文本段落缩放
        setSpan(span, start, end, flag)
    }?: config.textScaleXSpan?.also {span->
        //文本段落水平缩放
        setSpan(span, start, end, flag)
    }

    //首行缩进
    config.leadingMarginSpan?.also {span->
        setSpan(span, start, end, flag)
    }

    //列表标记
    config.listBulletSpan?.also {span->
        setSpan(span, start, end, 0)
    }

    //删除线
    if (config.isStrikethrough){
        setSpan(StrikethroughSpan(), start, end, flag)
    }
    //下划线
    if (config.isUnderline) {
        setSpan(UnderlineSpan(), start, end, flag)
    }

    if (config.isSuperscriptSpan){
        //上标
        setSpan(SuperscriptSpan(), start, end, flag)
    }else if (config.isSubscriptSpan){
        //下标
        setSpan(SubscriptSpan(), start, end, flag)
    }

    if (config.isBold && config.isItalic) {
        //添加粗斜体样式
        setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
    }else if (config.isBold){
        //粗体样式
        setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
    }else if (config.isItalic){
        //斜体样式
        setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
    }

    //字体样式
    config.fontTypefaceSpan?.also {span->
        setSpan(span, start, end, flag)
    }

    //文本尺寸样式
    config.textSizeSpan?.also {span->
        setSpan(span, start, end, flag)
    }

    //可点击样式
    config.clickableSpan?.also {span->
        setSpan(span, start, end, flag)
    }?:config.linkUrlSpan?.also {span->
        //超链接样式
        setSpan(span, start, end, flag)
    }

    //文段对齐样式
    config.alignSpan?.also {span->
        setSpan(span, start, end, flag)
    }

    //文本遮罩样式
    config.maskFilterSpan?.also {span->
        setSpan(span, start, end, flag)
    }
}
