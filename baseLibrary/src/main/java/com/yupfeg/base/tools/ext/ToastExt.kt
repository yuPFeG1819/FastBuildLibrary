package com.yupfeg.base.tools.ext

import android.app.Application
import android.view.Gravity
import androidx.annotation.StringRes
import com.hjq.toast.ToastUtils

/**
 * [Application]的拓展函数，初始化全局Toast
 */
fun Application.initToast(){
    ToastUtils.init(this)
}

/**
 * 显示默认短时间的toast
 * 	@param strContentId	文本提示信息 resId
 */
@Suppress("unused")
fun showShortToast(@StringRes strContentId: Int){
    ToastUtils.setGravity(Gravity.CENTER)
    ToastUtils.show(strContentId)
}

/**
 * 显示默认短时间的toast
 * 	@param content	文本提示信息
 */
@Suppress("unused")
fun showShortToast(content:String){
    ToastUtils.setGravity(Gravity.CENTER)
    ToastUtils.show(content)
}