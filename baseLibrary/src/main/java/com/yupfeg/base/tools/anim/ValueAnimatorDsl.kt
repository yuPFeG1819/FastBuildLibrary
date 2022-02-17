package com.yupfeg.base.tools.anim

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.PointF


// <editor-fold desc="常用拓展函数">

/**
 * [ValueAnimator]的DSL包装类，便于使用`Kotlin-dsl`模式设置属性动画
 * @author yuPFeG
 * @date 2022/02/16
 */
@Suppress("unused")
@SuppressLint("Recycle","unused")
open class ValueAnimatorDsl internal constructor() : AnimatorDsl(){

    override val animator: Animator = ValueAnimator()

    private val mValueAnimator : ValueAnimator
        get() = animator as ValueAnimator

    /**
     * 动画需要修改的属性值
     * - 优先级最高，如果在`ObjectAnimatorDsl`设置该属性，则其他快捷设置会被忽略
     * - 目前支持 [IntArray]、[FloatArray]、[PropertyValuesHolder]
     * - 另外也支持其他类型的[Array]数组，但需要手动配合设置[evaluator]，否则无法计算动画的值
     * */
    open var values : Any? = null
        set(value) {
            field = value
            value?:return
            setAnimValues(value)
        }

    /**
     * 额外设置动画的估值器，动画属性值的计算策略
     * - 在特殊类型的自定义属性，必须设置该值，否则无法正确进行动画
     * - 正常是不存在获取估值器的场景的
     * */
    open var evaluator : TypeEvaluator<*>? = null
        set(value) {
            field = value
            value?:return
            mValueAnimator.setEvaluator(value)
        }
    /**
     * 动画重复次数
     * */
    open var repeatCount : Int
        get() = mValueAnimator.repeatCount
        set(value) { mValueAnimator.repeatCount = value }

    /**
     * 动画重复模式
     * - 只允许两个值，
     * [ValueAnimator.RESTART] ：从头开始重复
     * [ValueAnimator.REVERSE] : 从结束位置反转开始重复
     * */
    open var repeatMode : Int
        get() = mValueAnimator.repeatMode
        set(value) {
            when(value){
                ValueAnimator.RESTART,ValueAnimator.REVERSE -> mValueAnimator.repeatMode = value
                else -> {}
            }
        }

    /**
     * 默认的动画值计算更新回调
     * */
    open var onUpdate : ((ValueAnimator)->Unit)? = null
        set(value) {
            field = value
            initDefValueAnimator()
        }

    /**
     * 是否已添加默认的动画值计算更新监听
     * */
    private var mDefaultUpdateListener : ValueAnimator.AnimatorUpdateListener? = null

    /**
     * 快捷初始化argb颜色变换动画
     * - 等效于[ValueAnimator.ofArgb]
     * */
    open fun initArgb(vararg value : Int){
        if (value.isEmpty()) return
        evaluator = ArgbEvaluator()
        values = value
    }

    /**
     * 快捷初始化坐标点动画
     * @param value 动画修改的坐标点对象
     * */
    open fun initPointF(vararg value : PointF){
        if (value.isEmpty()) return
        evaluator = PointFEvaluator()
        values = value
    }

    /**
     * 添加动画值计算更新的监听
     * @param listener 默认为null，添加默认监听，如果不为null，则添加新的回调监听
     * */
    open fun addUpdateListener(listener : ValueAnimator.AnimatorUpdateListener? = null){
        listener?.also {
            mValueAnimator.addUpdateListener(listener)
        }?:run {
            initDefValueAnimator()
        }
    }

    private fun initDefValueAnimator(){
        mDefaultUpdateListener?:run {
            mDefaultUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
                onUpdate?.invoke(animation)
            }
            mValueAnimator.addUpdateListener(mDefaultUpdateListener)
        }
    }

    /**
     * 设置动画属性值
     * @param values 表示值变化的数组与[PropertyValuesHolder]
     * */
    @Suppress("UNCHECKED_CAST")
    protected open fun setAnimValues(values : Any){
        when(values){
            is IntArray -> mValueAnimator.setIntValues(*values)
            is FloatArray -> mValueAnimator.setFloatValues(*values)
            is PropertyValuesHolder -> mValueAnimator.setValues(values)
            is Array<*> -> {
                if (values.isNullOrEmpty()) return
                val valuesHolders = values as? Array<PropertyValuesHolder>
                valuesHolders?.also {
                    mValueAnimator.setValues(*it)
                }?: run {
                    mValueAnimator.setObjectValues(*values)
                }
            }
            else -> {
                throw IllegalArgumentException("value animator unsupported value type")
            }
        }
    }

    // <editor-fold desc="动画进度控制">

    override fun start() {
        mValueAnimator.start()
    }

    override fun end() {
        mValueAnimator.end()
    }

    override fun cancel() {
        mValueAnimator.cancel()
    }

    override fun pause() {
        mValueAnimator.pause()
    }

    override fun resume() {
        mValueAnimator.resume()
    }

    // </editor-fold>
}