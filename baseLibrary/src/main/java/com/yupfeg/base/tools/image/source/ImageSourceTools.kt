package com.yupfeg.base.tools.image.source

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import com.yupfeg.base.tools.file.FileTools
import com.yupfeg.base.tools.file.getUriFromFile
import com.yupfeg.base.tools.image.ImageLoader
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 图片来源选择相关工具类
 * @author yuPFeG
 * @date 2019/11/8
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
@Deprecated("已废弃，使用ResultAPI")
object ImageSourceTools {
    /**相机拍照保存默认目录名称 */
    const val CAMERA_SAVE_DIR_NAME = "photo"
    /**用户头像剪裁默认保存目录名称 */
    const val USER_HEADER_CLIP_DIR_NAME = ".face"

    // <editor-fold desc="图片来源的请求码">
    /**跳转到选择本地相册的request code*/
    const val REQUEST_CODE_LOCAL_GALLERY = 0x1011
    /**跳转到调用系统相机的request code*/
    const val REQUEST_CODE_CAMERA = 0x1012
    /**跳转到调用系统剪切图片的request code*/
    const val REQUEST_CODE_CLIP_IMAGE = 0x1013

    // </editor-fold>

    /**默认的系统剪裁输出的x轴像素*/
    const val DEF_CLIP_OUTPUT_X = 800
    /**默认的系统剪裁输出的y轴像素*/
    const val DEF_CLIP_OUTPUT_Y = 800

    /**
     * 创建相机拍照的保存临时文件
     * @param context
     * @param cacheDirName 拍照图片保存目录名称，在app默认缓存目录下的二级目录
     * @return 用于保存拍照图片的临时文件
     */
    @JvmStatic
    @Suppress("unused")
    fun createCameraTempFile(context: Context,cacheDirName : String = CAMERA_SAVE_DIR_NAME) : File{
        //检查拍照文件保存的文件夹是否已创建
        val fileDir = FileTools.getDirOnAppFiles(context,cacheDirName)
        // 用日期作为文件名，确保唯一性
        val fileName = "camera_${getCurrDateToString()}.png"
        return File(fileDir, fileName)
    }

    /**
     * 创建系统剪裁图片保存的临时文件
     * @param context
     * @param zoomImgDirName 临时文件夹名称
     * @return 用于保存剪裁图片的临时文件
     */
    @JvmStatic
    @Suppress("unused")
    fun createSystemClipTempFile(context: Context,zoomImgDirName : String = USER_HEADER_CLIP_DIR_NAME) : File{
        //用户头像临时文件目录
        val clipFileDir = FileTools.getDirOnAppCacheDir(context,zoomImgDirName)
        // 用日期作为文件名，确保唯一性(临时文件)
        val fileName = "clip_${getCurrDateToString()}.png"
        //获取剪裁图片临时文件对象(临时文件地址)
        return File(clipFileDir, fileName)
    }

    /**
     * 获取当前时间字符串（yyyyMMddHHmmss格式）
     */
    @JvmStatic
    private fun getCurrDateToString() : String{
        val date = Date()
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
        return formatter.format(date)
    }

    /**
     * [ComponentActivity]的拓展函数，调用系统相机拍照
     * @param saveFile 拍照图片保存文件
     * @param requestCode 跳转到系统详细的请求码
     * */
    @JvmStatic
    @JvmOverloads
    @Suppress("unused")
    @Deprecated("已废弃，使用ResultAPI")
    fun navigateToSystemCamera(
        activity : ComponentActivity,
        saveFile : File,
        requestCode : Int = REQUEST_CODE_CAMERA
    ){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imgUri = getUriFromFile(activity, saveFile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0以上需要使用内容提供者授权分享uri
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * [Activity]的拓展函数，调起系统相册选择图片
     * @param requestCode 跳转到系统相册的请求码
     */
    @JvmStatic
    @JvmOverloads
    @Suppress("unused")
    @Deprecated("已废弃，使用ResultAPI")
    fun navigateToSystemGallery(
        activity: ComponentActivity,
        requestCode: Int = REQUEST_CODE_LOCAL_GALLERY
    ) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        activity.startActivityForResult(intent, requestCode)
    }



    /**
     * [Activity]的拓展函数，调用系统的裁剪图片方法实现
     * @param clipImgTempFile 剪裁图片保存的临时文件
     * @param requestCode 跳转到系统剪裁图片的请求码
     * @param data 待剪裁图片的地址uri （需要处理的图片uri在7.0以上需要使用FileProvider进行处理）
     * @param outputX 剪裁的图片宽度，默认为800
     * @param outputY 剪裁的图片高度，默认为800
     */
    @JvmStatic
    @Suppress("unused")
    @Deprecated("已废弃，使用ResultAPI")
    fun navigateToSystemZoom(
        activity: Activity,
        requestCode: Int = REQUEST_CODE_CLIP_IMAGE,
        data: Uri, clipImgTempFile: File,
        outputX : Int = 800, outputY : Int = 800
    ){
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(data, "image/*")
        // 设置裁剪
        intent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX)
        intent.putExtra("outputY", outputY)
        intent.putExtra("scale", true)
        //处理后的图片不进行在data中返回
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        //删除已存在的剪切图片
        if (clipImgTempFile.exists()) clipImgTempFile.delete()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0以上需要添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        //裁剪输出继续使用 Uri.fromFile(file)，不需要使用FileProvider
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clipImgTempFile))
        //跳转到系统剪裁页
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 剪裁图片文件
     * @param requestCode 跳转到系统剪裁图片的请求码
     * @param tempFileName 拍照保存文件的临时文件名，一般为拍照发起时的当前时间字符串
     * @param clipImgTempFile 剪裁图片保存的临时文件
     * @param outputX 剪裁的图片宽度，默认为800
     * @param outputY 剪裁的图片高度，默认为800
     * @return 是否成功跳转剪裁图片
     * */
    @JvmStatic
    @Suppress("unused")
    @Deprecated("已废弃，使用ResultAPI")
    fun clipImageFile(activity: Activity,requestCode: Int = REQUEST_CODE_CLIP_IMAGE,
                               tempFileName : String, clipImgTempFile: File,
                               outputX : Int = 800, outputY : Int = 800) : Boolean {
        if (tempFileName.isEmpty()) {
            return false
        }
        //获取拍照保存的本地文件对象
        val tempFile = getCameraPhotoFile(activity,tempFileName)
        if (!tempFile.exists()) {
            return false
        }
        //更新系统相册显示
        ImageLoader.updateSystemGallery(
            activity,
            tempFile.toString()
        )
        val fileUri = getUriFromFile(activity, tempFile)
        //跳转到系统剪裁页面
        navigateToSystemZoom(activity,requestCode = requestCode, clipImgTempFile = clipImgTempFile,
            data = fileUri, outputX = outputX,outputY = outputY)
        return true
    }

    /**
     * 获取相机拍照的本地保存文件
     * * 默认该文件保存在app私有目录下
     * */
    @JvmStatic
    fun getCameraPhotoFile(context: Context,tempFileName: String) : File{
        val fileDir = FileTools.getDirOnAppFiles(context, CAMERA_SAVE_DIR_NAME)
        return File(fileDir,tempFileName)
    }

    /**
     * 从缓存目录下，获取剪裁后的图片路径
     * @param zoomImgDirName 剪裁图片保存目录名称，在app默认缓存目录下的二级目录
     * @param fileName 剪裁图片保存的文件名
     * @return 剪裁后的图片完整地址
     */
    @JvmStatic
    @Suppress("unused")
    fun getClipFilePathFormZoomDir(context: Context,
                                   zoomImgDirName : String = USER_HEADER_CLIP_DIR_NAME,
                                   fileName : String) : String{
        //用户头像临时文件目录
        val clipFileDir = FileTools.getDirOnAppCacheDir(context,zoomImgDirName)
        //获取剪裁图片临时文件
        val clipFile = File(clipFileDir, fileName)
        return if (clipFile.exists()) clipFile.absolutePath else ""
    }

    /**
     * 清理已存在的剪裁后的图片文件
     * @param context
     * @param zoomImgDirName 剪裁图片保存目录名称，在app默认缓存目录下的二级目录
     * @param fileName 剪裁图片保存的文件名
     */
    @JvmStatic
    @Suppress("unused")
    fun cleanExitsClipFile(context: Context,
                           zoomImgDirName : String = USER_HEADER_CLIP_DIR_NAME,
                           fileName : String){
        if (fileName.isEmpty()) return
        //用户头像临时文件目录
        val clipFileDir = FileTools.getDirOnAppCacheDir(context,zoomImgDirName)
        //获取剪裁图片临时文件
        val clipFile = File(clipFileDir, fileName)
        if (clipFile.exists()){
            clipFile.delete()
        }
    }

}
