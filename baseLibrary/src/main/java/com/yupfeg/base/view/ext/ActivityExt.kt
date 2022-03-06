package com.yupfeg.base.view.ext

import android.app.Activity
import com.yupfeg.base.R

/**页面切换，页面进入动画类型*/
enum class TransitionAnimType{
    /**右侧滑动进入*/SLIDE_RIGHT_IN,/**底部滑动进入*/SLIDE_DOWN_IN,
    /**右侧滑动退出*/SLIDE_RIGHT_OUT,/**底部滑动退出*/SLIDE_DOWN_OUT
}

/**
 * [Activity]的拓展函数，执行切换动画
 * @param animType 动画类型[TransitionAnimType]
 */
@Suppress("unused")
fun Activity.setTransitionAnim(animType: TransitionAnimType){
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
