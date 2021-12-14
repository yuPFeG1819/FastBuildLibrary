package com.yupfeg.base.tools.system

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager


/**
 * 获取屏幕宽度
 * @param context
 */
@Suppress("unused")
fun getScreenWidth(context : Context) : Int{
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        val windowRect = windowManager.currentWindowMetrics.bounds
        windowRect.width()
    }else{
        val outMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(outMetrics)
        outMetrics.widthPixels
    }
}

/**
 * 获取屏幕的宽高
 * @param context
 * @return 包含的宽高[Pair],第一个值为屏幕宽度，第二个值为屏幕高度
 */
@Suppress("unused")
fun getScreenOutRect(context : Context) : Pair<Int,Int>{
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        val windowRect = windowManager.maximumWindowMetrics.bounds
        Pair(windowRect.width(),windowRect.height())
    }else{
        val outMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(outMetrics)
        Pair(outMetrics.widthPixels,outMetrics.heightPixels)
    }
}