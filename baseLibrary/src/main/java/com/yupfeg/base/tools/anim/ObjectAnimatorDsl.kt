package com.yupfeg.base.tools.anim

import android.animation.*
import android.annotation.SuppressLint
import android.view.View
import kotlin.NullPointerException

/**
 * [ObjectAnimator]的包装类，方便通过`kotlin-dsl`方式构建属性动画
 * @author yuPFeG
 * @date 2022/02/16
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@SuppressLint("Recycle")
class ObjectAnimatorDsl internal constructor() : ValueAnimatorDsl(){

    companion object {
        private const val TRANSLATION_X = "translationX"
        private const val TRANSLATION_Y = "translationY"
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
        private const val ALPHA = "alpha"
        private const val ROTATION = "rotation"
        private const val ROTATION_X = "rotationX"
        private const val ROTATION_Y = "rotationY"
    }

    override val animator: Animator = ObjectAnimator()

    private val mObjectAnimator : ObjectAnimator
        get() = animator as ObjectAnimator

    private var mPropertyHolderMap : MutableMap<String,PropertyValuesHolder> = mutableMapOf()

    private var isInit : Boolean = false

    /**
     * 属性动画作用目标，通常为视图对象
     * */
    var target : Any?
        get() = mObjectAnimator.target
        set(value) {
            mObjectAnimator.target = value
        }

    /**
     * 快捷提供[View]预设`translationX`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var translationX : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(TRANSLATION_X,*value)
            }?:run {
                mPropertyHolderMap.remove(TRANSLATION_X)
            }
        }

    /**
     * 快捷提供[View]预设`translationY`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var translationY : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(TRANSLATION_Y,*value)
            }?:run {
                mPropertyHolderMap.remove(TRANSLATION_Y)
            }
        }

    /**
     * 快捷提供[View]预设`scaleX`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var scaleX : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(SCALE_X,*value)
            }?:run {
                mPropertyHolderMap.remove(SCALE_X)
            }
        }

    /**
     * 快捷提供[View]预设`scaleY`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var scaleY : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(SCALE_Y,*value)
            }?:run {
                mPropertyHolderMap.remove(SCALE_Y)
            }
        }

    /**
     * 快捷提供[View]预设`alpha`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var alpha : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(ALPHA,*value)
            }?:run {
                mPropertyHolderMap.remove(ALPHA)
            }
        }

    /**
     * 快捷提供[View]预设`rotation`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var rotation : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(ROTATION,*value)
            }?:run {
                mPropertyHolderMap.remove(ROTATION)
            }
        }

    /**
     * 快捷提供[View]预设`rotation`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var rotationX : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(ROTATION_X,*value)
            }?:run {
                mPropertyHolderMap.remove(ROTATION_X)
            }
        }

    /**
     * 快捷提供[View]预设`rotation`属性动画的值变化
     * - 如果设置为null，会移除该动画
     * - 只在动画调用[start]前设置有效
     * */
    var rotationY : FloatArray ?= null
        set(value) {
            field = value
            value?.also {
                addFloatValues(ROTATION_Y,*value)
            }?:run {
                mPropertyHolderMap.remove(ROTATION_Y)
            }
        }

    /**
     * 添加指定属性名称的[Int]类型值变化
     * @param propertyName 属性名称，对应属性名称必须要存在`getter`\`setter`方法
     * @param value 值变化，如果只有一个值，如果只有一个值会通过`getter`方法获取当前值作为动画起始值
     * */
    fun addIntValues(propertyName : String,vararg value : Int){
        addIntValues(propertyName,null,*value)
    }

    /**
     * 添加指定属性名称的[Int]类型值变化
     * @param propertyName 属性名称，对应属性名称必须要存在`getter`\`setter`方法
     * @param evaluator 属性值变化的估值器，默认为null，不作特殊处理
     * @param value 值变化，如果只有一个值，如果只有一个值会通过`getter`方法获取当前值作为动画起始值
     * */
    fun addIntValues(
        propertyName : String,
        evaluator: TypeEvaluator<Int>? = null,
        vararg value : Int
    ){
        val holder = PropertyValuesHolder.ofInt(propertyName,*value)
        evaluator?.also { holder.setEvaluator(it) }
        mPropertyHolderMap[propertyName] = holder
    }

    /**
     * 添加指定属性名称的[Float]类型值变化
     * @param propertyName 属性名称，对应属性名称必须要存在`getter`\`setter`方法
     * @param value 值变化，最少需要2个值，如果只有一个值会通过`getter`方法获取当前值作为动画起始值
     * */
    fun addFloatValues(propertyName: String,vararg value : Float){
        addFloatValues(propertyName,null,*value)
    }

    /**
     * 添加指定属性名称的[Float]类型值变化
     * @param propertyName 属性名称，对应属性名称必须要存在`getter`\`setter`方法，
     * @param evaluator 属性值变化的估值器，默认为null，不作特殊处理
     * @param value 值变化，通常最少需要2个值，如果只有一个值会通过`getter`方法获取当前值作为动画起始值
     * */
    fun addFloatValues(
        propertyName: String,
        evaluator: TypeEvaluator<Float>? = null,
        vararg value : Float
    ){
        val holder = PropertyValuesHolder.ofFloat(propertyName,*value)
        evaluator?.also { holder.setEvaluator(it) }
        mPropertyHolderMap[propertyName] = holder
    }

    /**
     * 添加指定属性的其他类型值的变化
     * @param propertyName 属性名称，对应属性名称必须要存在`getter`\`setter`方法
     * @param evaluator 估值器，动画执行过程中值的计算策略
     * @param value 值变化，最少需要2个值，如果只有一个值会通过`getter`方法获取当前值作为动画起始值
     * */
    fun addObjectValues(propertyName: String,evaluator: TypeEvaluator<*>,vararg value: Any){
        mPropertyHolderMap[propertyName] = PropertyValuesHolder.ofObject(propertyName,evaluator,*value)
    }

    /**
     * 添加指定属性的关键帧
     * @param propertyName
     * @param value 关键帧对象[Keyframe]，确定动画运行进度对应的属性值
     * */
    fun addKeyframeValues(propertyName: String,vararg value : Keyframe){
        mPropertyHolderMap[propertyName] = PropertyValuesHolder.ofKeyframe(propertyName,*value)
    }


    override fun start() {
        target ?: throw NullPointerException("you should config anim target")

        if (values == null && mPropertyHolderMap.isNullOrEmpty()){
            throw NullPointerException("you should set anim value change array")
        }
        initPropertyValuesHolder()
        super.start()
    }

    /**
     * 初始化属性动画的参数变化
     * */
    internal fun initPropertyValuesHolder(){
        if (isInit) return
        if (mPropertyHolderMap.isNullOrEmpty()) {
            throw NullPointerException("you should set anim value change array")
        }

        val valuesHolderArray = mPropertyHolderMap.values.toTypedArray()
        this.values = valuesHolderArray
        isInit = true
    }

}