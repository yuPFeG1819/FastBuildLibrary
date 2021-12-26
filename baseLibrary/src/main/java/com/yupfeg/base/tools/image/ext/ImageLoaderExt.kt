package com.yupfeg.base.tools.image.ext

import android.content.Context
import android.media.MediaScannerConnection
import android.widget.ImageView
import com.yupfeg.base.tools.image.ImageLoader
import com.yupfeg.base.tools.image.opts.ImageLoadOptions

/**
 * 图片加载相关拓展函数
 * @author yuPFeG
 * @date 2021/03/23
 */

/**
 * [ImageView]拓展函数，加载图片url
 * @param config 图片加载配置[ImageLoadOptions]，以kotlin-dsl方式配置
 * */
fun ImageView.loadImage(config: ImageLoadOptions.()->Unit){
    ImageLoader.loadImageToView(this, config)
}

/**
 * 更新系统相册的显示
 * * 通常用于拍照后添加新图片到相册显示
 */
@Suppress("unused")
fun updateSystemGallery(context: Context, imgFilePath: String) {
    scannerByMedia(context, imgFilePath)
}

/** MediaScanner 扫描更新图库图片  */
private fun scannerByMedia(context: Context, path: String) {
    MediaScannerConnection.scanFile(context, arrayOf(path), null, null)
}