package com.yupfeg.base.tools.spannable

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.TextPaint
import android.text.style.*
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi

/**
 * SpannableString的样式配置，只作用于当前文本段落
 * @author yuPFeG
 * @date 2021/12/07
 */
@Suppress("unused")
class SpannableStringStyleConfig {

    /**文本颜色*/
    @JvmField
    @ColorInt
    var textColor : Int = 0
    /**文本背景色*/
    @JvmField
    @ColorInt
    var backgroundColor: Int = 0
    /**引用文本的前置垂直线颜色*/
    @JvmField
    @ColorInt
    var quoteLineColor: Int = 0

    /**文本缩进样式*/
    @JvmField
    var leadingMarginSpan : LeadingMarginSpan? = null
    /**列表标记，在文本前面加了列表缩进标识*/
    @JvmField
    var listBulletSpan : BulletSpan? = null

    /**文本缩放比例样式，控制整体缩放比例*/
    var textRelativeSizeSpan : RelativeSizeSpan? = null
        private set

    /**文本的水平方向缩放样式，只缩放x轴比例*/
    var textScaleXSpan : ScaleXSpan? = null
        private set

    /**是否添加删除线样式*/
    @JvmField
    var isStrikethrough : Boolean = false
    /**是否添加下划线样式*/
    @JvmField
    var isUnderline : Boolean = false
    /**是否添加文本上标样式，使所在文本移到基准线上方，通常需要配合修改文本大小的样式*/
    @JvmField
    var isSuperscriptSpan : Boolean = false
    /**是否添加文本下标样式，使文本移动到基准线下方，通常需要配合修改文本大小的样式*/
    @JvmField
    var isSubscriptSpan : Boolean = false

    /**文本段落字体样式*/
    var fontTypefaceSpan : TypefaceSpan? = null
        private set

    /**是否设置粗体*/
    @JvmField
    var isBold : Boolean = false
    /**是否设置斜体*/
    @JvmField
    var isItalic : Boolean = false

    /**文本尺寸样式，对文本段落单独修改文字大小*/
    var textSizeSpan : AbsoluteSizeSpan? = null
        private set

    /**文本超链接样式*/
    var linkUrlSpan : URLSpan? = null
        private set

    /**
     * 文本可点击样式，优先级高于超链接样式
     * * 使用该样式时，需要配合将TextView设置`movementMethod`属性设置为`LinkMovementMethod.getInstance()`，
     * 允许文本部分可点击才能触发，并且为了防止点击后变色，需要将TextView的`highlightColor`设置为透明色
     * */
    @JvmField
    var clickableSpan : ClickableSpan? = null

    /**
     * 文本段落对齐样式
     * * 需要外部文本控件宽度配合
     * */
    var alignSpan : AlignmentSpan? = null
        private set

    /**
     * 文本段落内的图片样式
     * */
    @JvmField
    var imageSpan : ImageSpan? = null

    /**
     * 文本遮罩样式
     * * 接受`MaskFilter`，对文本段落添加表面遮罩。
     * 如文字模糊遮罩，可使用`BlurMaskFilter`
     * */
    @JvmField
    var maskFilterSpan : MaskFilterSpan? = null


    /**
     * 设置文本段落缩进
     * @param first 段落首行缩进字符数
     * @param rest 段落其他行缩进字符数
     * */
    @JvmOverloads
    fun setLeadingMargin(first : Int, rest : Int = 0){
        leadingMarginSpan = LeadingMarginSpan.Standard(first, rest)
    }

    /**
     * 设置列表标记
     * @param gapWidth 项目符号点和文本段落之间的距离，单位px
     * @param color 标记颜色Int值，需使用`Color.parse`或`ContextCompat.getColor`获取
    * */
    fun addListBullet(gapWidth : Int,@ColorInt color : Int){
        listBulletSpan = BulletSpan(gapWidth, color)
    }

    /**
     * 设置文本整体缩放比例
     * @param proportion 缩放比例
     * */
    fun setProportion(proportion : Float){
        textRelativeSizeSpan = RelativeSizeSpan(proportion)
    }

    /**
     * 设置文本x轴缩放比例
     * @param xProportion 缩放比例
     * */
    fun setXProportion(xProportion : Float){
        textScaleXSpan = ScaleXSpan(xProportion)
    }

    /**
     * 设置系统文本字体样式
     * @param fontName 字体名称，仅支持系统字体，否则不生效
     * */
    fun setTextFont(fontName : String){
        fontTypefaceSpan = TypefaceSpan(fontName)
    }

    /**
     * 设置自定义的字体样式
     * @param typeface 字体样式
     * */
    @RequiresApi(Build.VERSION_CODES.P)
    fun setTypeface(typeface: Typeface){
        fontTypefaceSpan = TypefaceSpan(typeface)
    }

    /**设置目标文本的文本尺寸
     * @param size 文本尺寸，单位px
     * */
    fun setTextSize(size : Int){
        textSizeSpan = AbsoluteSizeSpan(size)
    }

    /**
     * 设置跳转链接地址
     * @param url 网页链接
     * */
    fun setLinkUrl(url : String){
        linkUrlSpan = URLSpan(url)
    }

    /**
     * 设置文本段落点击事件，优先级高于超链接样式
     * - 需要配合将TextView设置`movementMethod`属性设置为`LinkMovementMethod.getInstance()`，
     * 允许文本部分可点击才能触发点击事件。
     * - 并且为了防止点击后变色，需要将TextView的`highlightColor`设置为透明色
     * @param clickAction 文本点击事件
     * */
    fun setOnClick(clickAction : ()->Unit){
        clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                clickAction()
            }

            override fun updateDrawState(ds: TextPaint) {
                //禁用下划线
                ds.isUnderlineText = false
            }
        }
    }

    /**
     * 设置文本对齐样式
     * @param alignment 对齐位置
     *  * [Layout.Alignment.ALIGN_NORMAL]正常
     *  * [Layout.Alignment.ALIGN_OPPOSITE]相反
     *  * [Layout.Alignment.ALIGN_CENTER]居中
     * */
    fun setTextAlign(alignment: Layout.Alignment){
        alignSpan = AlignmentSpan.Standard(alignment)
    }

    /**
     * 设置本地图片资源替换当前文本段落
     * * 与其他样式可能冲突
     * @param context
     * @param resource 本地图片资源
     * @param verticalAlignment 图片在垂直方向的对齐方式
     * [DynamicDrawableSpan.ALIGN_BASELINE]或[DynamicDrawableSpan.ALIGN_BOTTOM]
     * */
    @JvmOverloads
    fun setImageResource(
        context : Context,
        @DrawableRes resource : Int,
        verticalAlignment : Int = DynamicDrawableSpan.ALIGN_BOTTOM
    ){
        imageSpan = ImageSpan(context,resource,verticalAlignment)
    }

    /**
     * 设置本地图片Drawable替换当前文本段落
     * * 与其他样式可能冲突
     * @param drawable 图片资源
     * @param verticalAlignment 图片在垂直方向的对齐方式
     * [DynamicDrawableSpan.ALIGN_BASELINE]或[DynamicDrawableSpan.ALIGN_BOTTOM]
     * */
    @JvmOverloads
    fun setImageDrawable(
        drawable : Drawable,
        verticalAlignment: Int = DynamicDrawableSpan.ALIGN_BASELINE
    ){
        imageSpan = ImageSpan(drawable, verticalAlignment)
    }

    /**
     * 设置文本模糊遮罩
     * @param radius 模糊半径，必须>0
     * @param style 模糊样式
     * */
    @JvmOverloads
    fun setBlurMask(radius : Float,style : BlurMaskFilter.Blur = BlurMaskFilter.Blur.NORMAL){
        if (radius <= 0) return
        maskFilterSpan = MaskFilterSpan(BlurMaskFilter(radius, style))
    }
}
