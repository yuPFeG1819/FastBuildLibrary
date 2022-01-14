package com.yupfeg.base.tools.system

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.FileProvider
import com.yupfeg.logger.ext.logd
import com.yupfeg.logger.ext.logw
import java.io.File

/**
 * app系统信息相关工具类
 * @author yuPFeG
 * @date 2020/04/23
 */
@Suppress("unused")
object AppUtils {
    /**当前进程名称*/
    private var mCurrProcessName : String? = null

    /**
     * 获取app系统版本号
     * @param context [Context]
     */
    @Suppress("unused")
    @JvmStatic
    fun getAppVersionCode(context: Context): Long {
        try {
            val packageInfo = context.applicationContext.packageManager
                ?.getPackageInfo(context.applicationContext.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                packageInfo?.longVersionCode?:1
            }else{
                @Suppress("DEPRECATION")
                packageInfo?.versionCode?.toLong()?:1
            }
        } catch (e: PackageManager.NameNotFoundException) {
            logw(e)
        }
        return 1
    }

    /**
     * 获取app系统版本名
     * @param context [Context]
     */
    @Suppress("unused")
    @JvmStatic
    fun getAppVersionName(context: Context): String {
        try {
            val packageInfo = context.applicationContext.packageManager
                ?.getPackageInfo(context.applicationContext.packageName, 0)
            return packageInfo?.versionName?:""
        } catch (e: PackageManager.NameNotFoundException) {
            logw(e)
        }
        return ""
    }

    /**
     * 安装APK
     * - 已适配Android7.0以上版本，增加FileProvider
     * @param context context
     * @param file 本地安装文件
     * */
    @SuppressLint("ObsoleteSdkInt")
    @JvmStatic
    fun install(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context,
                "${context.packageName}.fileProvider", file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        if (context is Activity){
            context.startActivity(intent)
        }else{
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * 重启App，并传递额外值
     * @param context
     * @param bundle
     * */
    @Suppress("unused")
    @JvmStatic
    fun reLaunchApp(context: Context, bundle: Bundle? = null){
        val packageName = context.packageName
        val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?:run {
            logd("无法获取${packageName}应用启动intent")
            return
        }
        // 设置flag
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        bundle?.also {
            launchIntent.putExtras(bundle)
        }
        logd("重启${packageName}的首页应用")
        context.startActivity(launchIntent)
    }

    /**
     * 返回app运行状态
     * @param context
     * @return int 1:前台 2:后台 0:不存在
     */
    @Suppress("unused")
    @JvmStatic
    fun getAppRunningStatus(context: Context): Int {
        val activityManager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        val packageName = context.packageName
        for (appProcessInfo in appProcesses) {
            if (appProcessInfo.processName.equals(packageName)){
                logd("此appimportace = ${appProcessInfo.importance},packageName = $packageName")
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE){
                    //应用已被销毁
                    return 0
                }

                return if (appProcessInfo.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    //应用处于前台
                    1
                }else {
                    //应用处于后台
                    2
                }
            }
        }
        return 0
    }
}