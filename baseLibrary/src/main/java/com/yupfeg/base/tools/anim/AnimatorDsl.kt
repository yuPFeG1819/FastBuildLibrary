package com.yupfeg.base.tools.anim

import android.animation.Animator
import android.animation.TimeInterpolator
import android.view.animation.LinearInterpolator

/**
 * [Animator]的DSL包装类，用于满足使用kotlin-dsl方式进行属性动画构建
 * @author yuPFeG
 * @date 2022/02/16
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class AnimatorDsl {
    abstract val animator : Animator

    /**
     * 动画执行时长
     * */
    var duration : Long
        get() = animator.duration
        set(value) { animator.duration = value }

    /**
     * 动画的速度模型
     * */
    var interpolator : TimeInterpolator?
        get() = animator.interpolator ?: LinearInterpolator()
        set(value) {
            animator.interpolator = value
        }

    /**
     * 动画开始前的延迟
     * */
    var startDelay : Long
        get() = animator.startDelay
        set(value) {
            animator.startDelay = value
        }

    /**默认的动画开始时执行动作*/
    var onStartAction : ((Animator)->Unit)? = null
        set(value){
            field = value
            initDefAnimListener()
        }

    /**默认的动画结束时执行动作*/
    var onEndAction : ((Animator)->Unit)? = null
        set(value){
            field = value
            initDefAnimListener()
        }

    /**默认的动画被取消时执行动作*/
    var onCancelAction : ((Animator)->Unit)? = null
        set(value){
            field = value
            initDefAnimListener()
        }

    /**默认的动画重复时执行动作*/
    var onRepeatAction : ((Animator)->Unit)? = null
        set(value){
            field = value
            initDefAnimListener()
        }

    /**默认的动画状态监听*/
    private var mDefAnimListener : Animator.AnimatorListener? = null

    /**默认的动画暂停时回调*/
    var onPauseAction : ((Animator)->Unit)? = null
        set(value) {
            field = value
            initDefPauseListener()
        }
    /**默认的动画恢复时回调*/
    var onResumeAction : ((Animator)->Unit)? = null
        set(value) {
            field = value
            initDefPauseListener()
        }

    /**默认的动画暂停监听*/
    private var mDefPauseListener : Animator.AnimatorPauseListener? = null

    // <editor-fold desc="动画监听函数">

    /**
     * 添加动画状态监听
     * @param listener 默认为null，添加默认监听（只生效添加一次），如果不为null,则添加对应新的状态监听
     * */
    fun addAnimListener(listener : Animator.AnimatorListener? = null){
        listener?.also {
            animator.addListener(it)
        }?: run {
            initDefAnimListener()
        }
    }

    /**
     * 初始化默认动画状态监听
     * */
    private fun initDefAnimListener(){
        mDefAnimListener?:run {
            mDefAnimListener = object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {
                    onStartAction?.invoke(animation)
                }

                override fun onAnimationEnd(animation: Animator) {
                    onEndAction?.invoke(animation)
                }

                override fun onAnimationCancel(animation: Animator) {
                    onCancelAction?.invoke(animation)
                }

                override fun onAnimationRepeat(animation: Animator) {
                    onRepeatAction?.invoke(animation)
                }

            }
            animator.addListener(mDefAnimListener)
        }
    }

    /**
     * 添加动画暂停监听
     * @param listener 暂停监听，默认为null，添加默认监听，只
     * */
    fun addPauseListener(listener : Animator.AnimatorPauseListener? = null){
        listener?.also {
            animator.addPauseListener(listener)
        }?: run {
            initDefPauseListener()
        }
    }

    /**
     * 初始化默认动画暂停监听
     */
    private fun initDefPauseListener(){
        mDefPauseListener?:run {
            mDefPauseListener = object : Animator.AnimatorPauseListener{
                override fun onAnimationPause(animation: Animator?) {
                    animation?:return
                    onPauseAction?.invoke(animation)
                }

                override fun onAnimationResume(animation: Animator?) {
                    animation?:return
                    onResumeAction?.invoke(animation)
                }

            }
        }
    }

    // </editor-fold>

    // <editor-fold desc="动画进度控制">

    abstract fun start()

    abstract fun cancel()

    abstract fun end()

    abstract fun pause()

    abstract fun resume()

    // </editor-fold>
}