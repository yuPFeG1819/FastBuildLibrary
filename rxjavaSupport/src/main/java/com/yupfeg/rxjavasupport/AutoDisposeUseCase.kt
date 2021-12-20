package com.yupfeg.rxjavasupport

import autodispose2.lifecycle.LifecycleScopeProvider
import com.yupfeg.base.domain.UseCase

/**
 * 提供AutoDispose，关联RxJava3的事件流生命周期
 * - 使子类能直接使用`autoDispose(this)`关联生命周期，自动管理`RxJava`事件流
 * - 在`ViewModel`使用时，需要使用`AutoDisposeViewModel`
 * @author yuPFeG
 * @date 2021/10/08
 */
@Suppress("unused")
open class AutoDisposeUseCase : UseCase(){

    /**提供给AutoDispose框架使用的作用域范围，由外部ViewModel提供作用域*/
    @JvmField
    var scopeProvider: LifecycleScopeProvider<ViewModelScopeEvent>? = null
}