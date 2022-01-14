package com.yupfeg.base.view.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.yupfeg.base.R

// <editor-fold desc="软键盘">

///**
// * [Activity]拓展函数，显示软键盘
// * @param view
// *  */
//@Suppress("unused")
//fun Activity.showInputKeyBoard(view: View) {
//    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    //显示软键盘
//    imm.showSoftInput(view, 0)
//}
//
///**
// * [Activity]拓展函数，隐藏软键盘
// * @param view
// */
//@Suppress("unused")
//fun Activity.hideInputKeyBoard(view: View) {
//    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    //强制隐藏键盘
//    imm.hideSoftInputFromWindow(view.windowToken, 0)
//}

// </editor-fold>

// <editor-fold desc="预设activity跳转与结束方法">

/**
 * [Activity]拓展函数，关闭当前activity,执行预先设置好的退出动画
 * @param outAnimType 预设退出动画类型 [TransitionAnimType]，默认为[TransitionAnimType.SLIDE_RIGHT_OUT]
 */
fun Activity.finishWithAnim(
    outAnimType : TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_OUT
) {
    finish()
    performTransitionAnim(outAnimType)
}

/**
 * [Activity]拓展函数，使用预设的页面启动动画，启动跳转activity
 * @param clazz 目标activity
 * @param bundle 跳转传递值
 * @param inAnimType 启动动画跳转类型 [TransitionAnimType]，默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Suppress("unused")
fun Activity.startActivityWithAnim(
    clazz: Class<*>, bundle: Bundle? = null,
    inAnimType : TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    val intent = Intent(this, clazz)
    bundle?.let { intent.putExtras(it) }
    startActivityWithAnim(intent, inAnimType)
}

/**
 * [Activity]拓展函数，使用预设的页面启动动画，启动跳转activity
 * @param intent 跳转意图
 * @param inAnimType 跳转动画类型 [TransitionAnimType]，默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Suppress("unused")
fun Activity.startActivityWithAnim(
    intent: Intent, inAnimType: TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    startActivity(intent)
    performTransitionAnim(inAnimType)
}

/**
 * [Activity]拓展函数，使用预设的页面启动动画，启动跳转activity并设置返回请求码
 * @param clazz 目标activity
 * @param bundle 跳转传递值
 * @param requestCode 跳转意图请求码
 * @param inAnimType 页面启动动画类型 [TransitionAnimType],默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Suppress("unused")
@Deprecated("已废弃",replaceWith = ReplaceWith(
    "请使用ResultAPI",
))
fun Activity.startActivityWithAnimForResult(
    clazz: Class<*>, bundle: Bundle?, requestCode: Int,
    inAnimType : TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    val intent = Intent(this, clazz)
    bundle?.let { intent.putExtras(it) }
    startActivityWithAnimForResult(intent, requestCode, inAnimType)
}

/**
 * [Activity]拓展函数，使用预设的页面启动动画，启动跳转activity并设置返回请求码
 * @param intent 跳转意图
 * @param requestCode 跳转意图请求码
 * @param inAnimType 页面启动动画类型 [TransitionAnimType]，默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Deprecated("已废弃",replaceWith = ReplaceWith(
    "请使用ResultAPI",
))
fun Activity.startActivityWithAnimForResult(
    intent: Intent, requestCode: Int,
    inAnimType : TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    startActivityForResult(intent, requestCode)
    performTransitionAnim(inAnimType)
}

// </editor-fold desc="预设activity启动">

/**页面切换，页面进入动画类型*/
enum class TransitionAnimType{
    /**右侧滑动进入*/SLIDE_RIGHT_IN,/**底部滑动进入*/SLIDE_DOWN_IN,
    /**右侧滑动退出*/SLIDE_RIGHT_OUT,/**底部滑动退出*/SLIDE_DOWN_OUT
}

/**
 * [Activity]的拓展函数，执行切换动画
 * @param animType 动画类型[TransitionAnimType]
 */
fun Activity.performTransitionAnim(animType: TransitionAnimType){
    when(animType){
        //底部滑动退出动画
        TransitionAnimType.SLIDE_DOWN_OUT ->{
            overridePendingTransition(R.anim.push_keep_still, R.anim.push_down_out)
        }
        //左侧进入，右侧滑出
        TransitionAnimType.SLIDE_RIGHT_OUT ->{
            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out)
        }
        //左侧滑出，右侧滑入动画
        TransitionAnimType.SLIDE_RIGHT_IN -> {
            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out)
        }
        //底部滑动进入动画
        TransitionAnimType.SLIDE_DOWN_IN -> {
            overridePendingTransition(R.anim.push_down_in, R.anim.push_keep_still)
        }
    }
}
