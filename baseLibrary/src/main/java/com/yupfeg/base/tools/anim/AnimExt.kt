package com.yupfeg.base.tools.anim

import android.animation.AnimatorSet

/**
 * 构建值动画
 * @param config dsl配置动画
 * @return 属性动画的dsl包装类
 */
fun valueAnim(config : ValueAnimatorDsl.()->Unit) : ValueAnimatorDsl{
    return ValueAnimatorDsl().apply(config)
}

/**
 * 构建属性动画
 * @param config dsl配置动画
 * @return 属性动画的dsl包装类
 * */
fun objectAnim(config: ObjectAnimatorDsl.() -> Unit) : ObjectAnimatorDsl{
    return ObjectAnimatorDsl().apply(config).also{
        //初始化属性动画的值变化
        it.initPropertyValuesHolder()
    }
}

/**
 * kotlin-DSL方式构建动画组合对象，作为组合动画序列的外层。
 * 例如 ：
 * ```
 *  animSet {
 *   duration = 1000
 *   playValueAnim{
 *      ...
 *   } with objectAnim {
 *
 *   } before valueAnim {
 *
 *   }
 *  }.start()
 * ```
 *
 * @param init 初始化动画组合
 * */
fun animSet(init : AnimatorSet.()->Unit) : AnimatorSet{
    return AnimatorSet().apply(init)
}

/**
 * [AnimatorSet]的拓展函数，以`ValueAnimator`作为依赖锚点，构建动画序列的依赖关系
 * @param config 锚点动画的dsl配置
 * */
@Suppress("unused")
fun AnimatorSet.playValueAnim(config: ValueAnimatorDsl.() -> Unit) : AnimatorSet.Builder{
    return play(valueAnim(config).animator)
}

/**
 * [AnimatorSet]的拓展函数，以`ObjectAnimator`作为依赖锚点，构建动画序列的依赖关系
 * @param config 锚点动画的dsl配置
 * */
@Suppress("unused")
fun AnimatorSet.playObjectAnim(config: ObjectAnimatorDsl.() -> Unit) : AnimatorSet.Builder{
    return play(objectAnim (config).animator)
}

/**
 * [AnimatorSet]的拓展函数，以第一个动画对象作为锚点，建立同时执行的动画依赖关系
 * @param wrapper 属性动画的包装类
 * */
@Suppress("unused")
fun AnimatorSet.playTogether(vararg wrapper: AnimatorDsl) {
    if (wrapper.isNullOrEmpty()) return
    val builder : AnimatorSet.Builder = play(wrapper[0])
    for (index in 1..wrapper.lastIndex){
        builder.with(wrapper[index])
    }
}

/**
 * [AnimatorSet]的拓展函数，已第一个动画作为锚点，建立顺序执行的动画依赖关系
 * @param wrapper 属性动画的包装类
 * */
@Suppress("unused")
fun AnimatorSet.playSequentially(vararg wrapper: AnimatorDsl){
    if (wrapper.isNullOrEmpty()) return
    if (wrapper.size == 1){
        play(wrapper[0])
        return
    }

    for (i in 0..wrapper.lastIndex){
        play(wrapper[i]).before(wrapper[i+1])
    }
}

/**
 * [AnimatorSet]的拓展函数，构建动画依赖序列
 * @param wrapper 锚点动画，[AnimatorDsl]属性动画的包装类
 * */
@Suppress("unused")
fun AnimatorSet.play(wrapper: AnimatorDsl) : AnimatorSet.Builder{
    return play(wrapper.animator)
}

/**
 * [AnimatorSet.Builder]的拓展中缀函数，依赖动画与锚点动画同时执行
 * @param wrapper 依赖动画，可以直接使用[valueAnim]或[objectAnim]函数构建依赖动画
 * */
@Suppress("unused")
infix fun AnimatorSet.Builder.with(wrapper: AnimatorDsl) : AnimatorSet.Builder{
    return this.with(wrapper.animator)
}

/**
 * [AnimatorSet.Builder]的拓展中缀函数，在锚点动画之后执行
 * @param wrapper 依赖动画，可以直接使用[valueAnim]或[objectAnim]函数构建依赖动画
 * */
@Suppress("unused")
infix fun AnimatorSet.Builder.after(wrapper: AnimatorDsl) : AnimatorSet.Builder{
    return this.after(wrapper.animator)
}

/**
 * [AnimatorSet.Builder]的拓展中缀函数，在锚点动画之前执行
 * @param wrapper 依赖动画，可以直接使用[valueAnim]或[objectAnim]函数构建依赖动画
 * */
@Suppress("unused")
infix fun AnimatorSet.Builder.before(wrapper: AnimatorDsl) : AnimatorSet.Builder{
    return this.before(wrapper.animator)
}
