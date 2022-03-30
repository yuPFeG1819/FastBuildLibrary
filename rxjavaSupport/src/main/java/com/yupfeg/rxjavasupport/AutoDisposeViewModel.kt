/*
 * Copyright (c) 2018. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yupfeg.rxjavasupport

import androidx.lifecycle.ViewModel
import autodispose2.lifecycle.CorrespondingEventsFunction
import autodispose2.lifecycle.LifecycleEndedException
import autodispose2.lifecycle.LifecycleScopeProvider
import com.yupfeg.base.domain.UseCase
import com.yupfeg.base.domain.UseCaseQueue
import com.yupfeg.rxjavasupport.domain.AutoDisposeUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * 集成了AutoDispose框架的ViewModel，
 * 实现[LifecycleScopeProvider],能够自动管理ViewModel内调用的RxJava订阅
 */
@Suppress("unused")
open class AutoDisposeViewModel : ViewModel(), LifecycleScopeProvider<ViewModelScopeEvent> {

    companion object {
        /**
         * Function of current event -> target disposal event. ViewModel has a very simple lifecycle.
         * It is created and then later on cleared. So we only have two events and all subscriptions
         * will only be disposed at [ViewModelScopeEvent.CLEARED].
         */
        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<ViewModelScopeEvent> { event ->
            when (event) {
                ViewModelScopeEvent.CREATED -> ViewModelScopeEvent.CLEARED
                else -> throw LifecycleEndedException(
                    "Cannot bind to ViewModel lifecycle after onCleared.")
            }
        }
    }

    private var isAddUseCase = false

    private val mUseCaseScheduler : UseCaseQueue by  lazy(LazyThreadSafetyMode.NONE){
        UseCaseQueue()
    }

    // Subject backing the auto disposing of subscriptions.
    private val lifecycleEvents = BehaviorSubject.createDefault(ViewModelScopeEvent.CREATED)

    /**重置viewModel lifecycle*/
    fun resetLifecycle(){
        lifecycleEvents.onNext(ViewModelScopeEvent.CREATED)
    }

    // <editor-fold desc="LifecycleScopeProvider抽象方法实现">

    /**
     * The observable representing the lifecycle of the [ViewModel].
     *
     * @return [Observable] modelling the [ViewModel] lifecycle.
     */
    override fun lifecycle(): Observable<ViewModelScopeEvent> {
        return lifecycleEvents.hide()
    }

    /**
     * Returns a [CorrespondingEventsFunction] that maps the
     * current event -> target disposal event.
     *
     * @return function mapping the current event to terminal event.
     */
    override fun correspondingEvents(): CorrespondingEventsFunction<ViewModelScopeEvent> {
        return CORRESPONDING_EVENTS
    }

    override fun peekLifecycle(): ViewModelScopeEvent? {
        return lifecycleEvents.value
    }

    // </editor-fold>

    protected fun addUseCase(useCase: UseCase) {
        (useCase as? AutoDisposeUseCase)?.apply {
            //赋值绑定的用例类对应的autoDispose作用域
            this.scopeProvider = this@AutoDisposeViewModel
        }
        mUseCaseScheduler.add(useCase)
        if (!isAddUseCase) isAddUseCase = true
    }

    /**
     * Emit the [ViewModelScopeEvent.CLEARED] event to
     * dispose off any subscriptions in the ViewModel.
     */
    override fun onCleared() {
        lifecycleEvents.onNext(ViewModelScopeEvent.CLEARED)
        mUseCaseScheduler.cancelAndRemoveAll()
    }


}

/**
 * The events that represent the lifecycle of a `ViewModel`.
 *
 * The `ViewModel` lifecycle is very simple. It is created
 * and then allows you to clean up any resources in the
 * `ViewModel.onCleared` method before it is destroyed.
 */
enum class ViewModelScopeEvent {
    CREATED, CLEARED
}