package com.yupfeg.base.tools.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.yupfeg.logger.ext.logd
import com.yupfeg.logger.ext.logw
import com.yupfeg.base.tools.file.getRealFilePathFromFileUri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt


/**
 * bitmap处理相关提取的顶层方法
 * @author yuPFeG
 * @date 2019/9/9
 */
/**上传图片的尺寸 */
private const val UPLOAD_IMG_WIDTH = 1080
private const val UPLOAD_IMG_HEIGHT = 1920
/**
 * 默认最大图片大小300k
 */
private const val MAX_COMPRESS_IMG_SIZE = 300

/**
 * 压缩图片Bitmap至指定大小
 * *耗时操作，最好在子线程中调用
 * @param imgPath 图片路径
 * @param maxSize 最大图片大小（k）
 * @return 图片压缩后的byteArray输出流
 */
@Throws(IOException::class)
fun compressImageToByteStream(imgPath: String, maxSize: Int): ByteArrayOutputStream {
    //如果最大压缩大小为0，则取默认值
    val maxCompressSize = if (maxSize <= 0 || maxSize < MAX_COMPRESS_IMG_SIZE)
        MAX_COMPRESS_IMG_SIZE else maxSize
    val image = getBitmapFromImagePath(imgPath, UPLOAD_IMG_WIDTH, UPLOAD_IMG_HEIGHT)
    val degree = getBitmapDegree(imgPath)
    //如果图片被翻转了,需要转回去
    val lastImg: Bitmap = if (degree > 0) rotateBitmapByDegree(image, degree) else image
    val os = ByteArrayOutputStream()
    var options = 100
    var compressSize = 0
    // Store the bitmap into output stream(no compress)
    lastImg.compress(Bitmap.CompressFormat.JPEG, options, os)
    logd("file size before Compress :" + os.toByteArray().size)
    // Compress by loop
    while (os.toByteArray().size / 1024 > maxCompressSize) {
        compressSize++
        options -= 1
        logd("Compress number:$compressSize[Compress options：]$options")
        os.reset()
        lastImg.compress(Bitmap.CompressFormat.JPEG, options, os)
    }
    logd("file size after Compress" + os.toByteArray().size)
    //回收无用bitmap
    if (!image.isRecycled) {
        image.recycle()
    }
    if (!lastImg.isRecycled) {
        lastImg.recycle()
    }
    return os
}

/**
 * 从文件uri获取bitmap
 * * 耗时操作，最好在子线程执行
 * @param context [Context]
 * @param uri 图片uri
 * @param reqWidth 目标图片宽度
 * @param reqHeight 目标图片高度
 * @param maxCompressSize 最大图片大小，默认[MAX_COMPRESS_IMG_SIZE]
 */
@Suppress("unused")
@Throws(IOException::class)
fun getCompressBitmapFromUri(context: Context,uri: Uri, reqWidth: Int, reqHeight: Int,
                     maxCompressSize: Int = MAX_COMPRESS_IMG_SIZE) : Bitmap?{
    val imgPath = getRealFilePathFromFileUri(context,uri)
    if (imgPath.isEmpty()) return null
    val newOpts = BitmapFactory.Options()
    //--------------只读取，图片的尺寸信息,计算缩放比例------------
    newOpts.inJustDecodeBounds = true
    newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888
    BitmapFactory.decodeFile(imgPath, newOpts)
    logd("Bitmap Original size -h：${newOpts.outHeight} " +
            "-w: ${newOpts.outWidth}")
    newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight)
    //-------------根据缩放比例，获得bitmap实例---------------
    val originBitmap = getBitmapFromImagePath(imgPath, reqWidth, reqHeight)
    val degree = getBitmapDegree(imgPath)
    //如果图片被翻转了,需要转回去再上传
    val rotateBitmap: Bitmap = if (degree > 0) rotateBitmapByDegree(originBitmap, degree)
    else originBitmap
    val compressBitmap = compressBitmap(bitmap = rotateBitmap,
        maxCompressSize = maxCompressSize)
    //回收无用bitmap
    if (!originBitmap.isRecycled){
        originBitmap.recycle()
    }
    if (!rotateBitmap.isRecycled){
        rotateBitmap.recycle()
    }
    return compressBitmap
}

/**
 * 获得指定图片路径的bitmap实例
 * @param imgPath 本地图片路径
 * @param reqWidth 目标图片宽度
 * @param reqHeight 目标图片高度
 * @return bitmap
 */
fun getBitmapFromImagePath(imgPath: String, reqWidth: Int, reqHeight: Int): Bitmap {
    val newOpts = BitmapFactory.Options()
    //--------------只读取，图片的尺寸信息,计算缩放比例------------
    newOpts.inJustDecodeBounds = true
    newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888
    BitmapFactory.decodeFile(imgPath, newOpts)
    logd("Bitmap Original size -h：" + newOpts.outHeight + "-w:" + newOpts.outWidth)
    newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight)
    //-------------根据缩放比例，获得图片实例---------------
    newOpts.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeFile(imgPath, newOpts)
    logd("Bitmap Compress size -h：" + newOpts.outHeight + "-w:" + newOpts.outWidth)
    return bitmap
}

/**
 * 获取指定图片uri的bitmap实例
 * @param uri 图片uri
 * @param reqWidth 目标图片宽度
 * @param reqHeight 目标图片高度
 */
@Suppress("unused")
fun getBitmapFromUri(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int) : Bitmap?{
    val input : InputStream = context.contentResolver.openInputStream(uri) ?: return null
    val newOpts = BitmapFactory.Options()
    //--------------只读取，图片的尺寸信息,计算缩放比例------------
    newOpts.inJustDecodeBounds = true
    newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888
    BitmapFactory.decodeStream(input,null, newOpts)
    logd("Bitmap Original size -h：${newOpts.outHeight} " +
            "-w: ${newOpts.outWidth}")
    newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight)
    //-------------根据缩放比例，获得图片实例---------------
    newOpts.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeStream(input,null, newOpts)
    logd("Bitmap Compress size -h：" + newOpts.outHeight + "-w:" + newOpts.outWidth)
    return bitmap
}

/**
 * 压缩图片bitmap
 * * 耗时操作，最好放到子线程执行
 * @param bitmap 原始bitmap
 * @param maxCompressSize 最大压缩尺寸
 * @return 压缩后的bitmap
 */
private fun compressBitmap(bitmap: Bitmap,maxCompressSize : Int) : Bitmap?{
    val os = ByteArrayOutputStream()
    var options = 100
    var compressSize = 0
    // Store the bitmap into output stream(no compress)
    bitmap.compress(Bitmap.CompressFormat.JPEG, options, os)
    logd("file size before Compress :" + os.toByteArray().size)
    // Compress by loop
    while (os.toByteArray().size / 1024 > maxCompressSize) {
        compressSize++
        options -= 1
        logd("Compress number:$compressSize[Compress options：]$options")
        os.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os)
    }
    logd("file size after Compress" + os.toByteArray().size)
    //把压缩后的数据流存放到ByteArrayInputStream中
    val bitArrayInput = ByteArrayInputStream(os.toByteArray())
    if (!bitmap.isRecycled){
        bitmap.recycle()
    }
    //把ByteArrayInputStream数据生成图片
    return BitmapFactory.decodeStream(bitArrayInput, null, null)
}

/**
 * 计算图片的缩放值
 * @param options BitmapFactory.Options
 * @param reqWidth 目标宽度
 * @param reqHeight    目标高度
 */
private fun calculateInSampleSize(options: BitmapFactory.Options,
                                  reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
        inSampleSize = if (heightRatio < widthRatio) widthRatio else heightRatio
    }
    return inSampleSize
}

/**
 * 读取图像的旋转度
 */
fun getBitmapDegree(path: String): Int {
    var degree = 0
    try {
        val exifInterface = ExifInterface(path)
        when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            else -> {
            }
        }
    } catch (e: IOException) {
        logw(e)
    }

    return degree
}

/**
 * 将图片按照某个角度进行旋转
 *
 * @param bm
 * 需要旋转的图片(会被回收掉)
 * @param degree
 * 旋转角度
 * @return 旋转后的图片
 */
fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
    var returnBm: Bitmap? = null
    // 根据旋转角度，生成旋转矩阵
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    try {
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width,
                bm.height, matrix, true)
    } catch (e: OutOfMemoryError) {
        logw(e)
    }

    if (returnBm == null) {
        returnBm = bm
    }
    if (bm != returnBm) {
        bm.recycle()
    }
    return returnBm
}

/**
 * 简单裁剪bitmap
 * @param originBitmap 原始bitmap
 * @param targetWidth 目标图片宽度
 * @param targetHeight 目标图片高度
 * */
fun sampleClipBitmap(
    originBitmap : Bitmap,
    targetWidth : Int,
    targetHeight : Int
) : Bitmap{
    return Bitmap.createBitmap(originBitmap,0,0,targetWidth,targetHeight)
}
