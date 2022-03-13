package com.yupfeg.base.tools.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.yupfeg.base.tools.image.opts.ImageLoadOptions

/**
 * 图片加载功能抽象接口
 * @author yuPFeG
 * @date 2021/03/22
 */
interface ImageLoadable {
    /**
     * 加载图片url
     * @param imageView
     * @param config 图片加载配置
     * */
    fun loadImageToView(imageView: ImageView, config : ImageLoadOptions)

    /**
     * 加载bitmap
     * @param config 图片加载配置
     * @param bitmapReadyAction 加载完成bitmap的执行动作
     * @param loadStartAction 初始加载的显示回调
     * @param loadErrorAction 加载失败的显示回调
     * */
    fun loadBitmap(
        context: Context,
        config: ImageLoadOptions,
        bitmapReadyAction : (Bitmap)->Unit,
        loadStartAction : ((Drawable?)->Unit)? = null,
        loadErrorAction : ((Drawable?)->Unit)? = null
    )

    /**
     * 暂停指定作用域范围内的图片加载请求
     * @param context [Context]生命周期作用域
     * */
    fun pauseRequest(context: Context)

    /**
     * 恢复指定作用域范围的图片加载请求
     * @param context [Context]生命周期作用域
     */
    fun resumeRequest(context: Context)

    /**
     * 清理指定View的bitmap缓存
     * @param imageView
     * */
    fun cleanTargetViewBitmapCache(imageView: ImageView)

    /**
     * 清理所有图片缓存
     * */
    fun cleanAllCache(context: Context)

}