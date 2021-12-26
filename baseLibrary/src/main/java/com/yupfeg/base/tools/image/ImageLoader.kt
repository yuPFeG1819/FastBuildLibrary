package com.yupfeg.base.tools.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.widget.ImageView
import com.yupfeg.base.tools.image.opts.ImageLoadOptions

/**
 * 图片加载帮助类
 * @author yuPFeG
 * @date 2021/03/22
 */
@Suppress("unused")
object ImageLoader {

    private val imageLoadHelper : ImageLoadable by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        //TODO 可以通过工厂模式+策略模式，创建对应的实际加载策略
        GlideLoadHelper()
    }

    /**
     * 加载图片url
     * @param imageView
     * @param config 图片加载配置[ImageLoadOptions]，以kotlin-dsl方式配置
     * */
    @JvmStatic
    fun loadImageToView(imageView: ImageView, config : ImageLoadOptions.()->Unit){
        imageLoadHelper.loadImageToView(imageView,ImageLoadOptions().apply(config))
    }

    /**
     * 加载bitmap
     * @param context
     * @param config 图片加载配置[ImageLoadOptions]，以kotlin-dsl方式配置
     * @param onBitmapReady bitmap加载完成执行函数，需要注意bitmap销毁
     * @param loadStartAction 加载前的显示回调
     * @param loadErrorAction 加载失败的显示回调
     */
    fun loadBitmap(
        context: Context,
        config: ImageLoadOptions.() -> Unit,
        onBitmapReady : (Bitmap)->Unit,
        loadStartAction : ((Drawable?)->Unit)? = null,
        loadErrorAction : ((Drawable?)->Unit)? = null
    ){
        imageLoadHelper.loadBitmap(
            context,
            ImageLoadOptions().apply(config),
            onBitmapReady,
            loadStartAction,
            loadErrorAction
        )
    }

    /**
     * 清理指定View的Bitmap
     * @param imageView 指定ImageView
     */
    @Suppress("unused")
    fun clearBitmapCacheOnView(imageView: ImageView) {
        imageLoadHelper.cleanTargetViewBitmapCache(imageView)
    }

    /**
     * 清理所有本地缓存
     * * 最好在子线程执行，耗时操作
     * @param context
     * */
    fun cleanAllCache(context: Context){
        imageLoadHelper.cleanAllCache(context)
    }

    /**
     * 更新系统相册的显示
     */

    @Suppress("unused")
    fun updateSystemGallery(context: Context, imgFilePath: String) {
        scannerByMedia(context, imgFilePath)
    }

    /** MediaScanner 扫描更新图库图片  */
    private fun scannerByMedia(context: Context, path: String) {
        MediaScannerConnection.scanFile(context, arrayOf(path), null, null)
    }
}