package com.yupfeg.base.tools.image.opts

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes

/**
 * kotlin-dsl方式的图片加载配置类
 * @author yuPFeG
 * @date 2021/03/22
 */
class ImageLoadOptions{
    companion object {
        /**原始图片尺寸大小*/
        const val SIZE_ORIGINAL = -0x1113

        /**大小跟随视图*/
        const val SIZE_FROM_TARGET_VIEW = -0x1123
    }

    /**图片加载地址*/
    @JvmField
    var imgPath: String = ""
    /**图片加载uri,优先级比[imgPath]高*/
    @JvmField
    var imgUri : Uri ? = null
    /**加载本地图片resId，优先级最高，只要不为0，则加载本地图片*/
    @JvmField
    @DrawableRes var localDrawableId : Int = 0

    /**占位图resId*/
    @JvmField
    @DrawableRes var placeholderId : Int = 0
    /**占位图drawable*/
    @JvmField
    var placeholder: Drawable? = null

    /**错误图resId*/
    @JvmField
    @DrawableRes var errorId : Int = 0
    /**错误图drawable*/
    @JvmField
    var error : Drawable? = null

    /**是否跳过内存缓存，默认为false*/
    @JvmField
    var isSkipMemoryCache : Boolean = false
    /**是否跳过本地文件缓存，默认为false*/
    @JvmField
    var isSkipDiskCache : Boolean = false
    /**
     * 是否先显示缩略图的缩略比例，
     * 默认为1f，不会首先加载缩略图，在[0,1)之间才会加载缩略图
     * */
    @JvmField
    var thumbnail : Float = 1f
    /**圆角图片的弧度，单位dp*/
    @JvmField
    var radius : Int = 0
    /**圆角（圆形）类型*/
    @JvmField
    var roundCornerType: ImageRoundCornerType = ImageRoundCornerType.NONE

    /**加载缩放类型*/
    @JvmField
    var scaleType: ImageScaleType = ImageScaleType.CENTER_CROP
    /**加载的图片宽度，默认跟随视图大小*/
    @JvmField
    var overrideWidth : Int = SIZE_FROM_TARGET_VIEW
    /**加载的图片高度，默认跟随视图大小*/
    @JvmField
    var overrideHeight : Int = SIZE_FROM_TARGET_VIEW
}

/**图片加载的圆角类型*/
@Suppress("unused")
enum class ImageRoundCornerType{
    //无圆角（默认）
    NONE,
    //圆形
    CIRCLE,
    //全部四个圆角
    ALL,
    //四边的两个圆角
    TOP,BOTTOM,LEFT,RIGHT,
    //单独一个圆角
    TOP_LEFT,TOP_RIGHT, BOTTOM_LEFT,BOTTOM_RIGHT,
}

/**图片加载缩放类型*/
enum class ImageScaleType{
    CENTER_CROP,CENTER_INSIDE,FIT_CENTER
}