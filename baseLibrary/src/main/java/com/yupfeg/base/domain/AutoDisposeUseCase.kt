package com.yupfeg.base.domain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.lifecycle.CorrespondingEventsFunction
import autodispose2.lifecycle.LifecycleScopeProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * 实现AutoDispose，关联RxJava3的事件流生命周期
 * * 修改自[AndroidLifecycleScopeProvider]，实现`AutoDispose`的`LifecycleScopeProvider`接口，
 * 使子类能直接使用`autoDispose(this)`关联生命周期，自动管理`RxJava`事件流
 * * 在`ViewModel`使用时，需要绑定到视图作用域的[Lifecycle]
 * @author yuPFeG
 * @date 2021/10/08
 */
@Suppress("unused")
open class AutoDisposeUseCase : LifecycleUseCase(), LifecycleScopeProvider<Lifecycle.Event>{
    // Subject backing the auto disposing of subscriptions.
    private val mLifecycleEventSubject = BehaviorSubject.createDefault(Lifecycle.Event.ON_CREATE)

    /**
     * 最终结束的RxJava生命周期事件
     * * 即自动结束RxJava数据流的目标生命周期
     * */
    protected open val correspondingEvents =
        CorrespondingEventsFunction { _: Lifecycle.Event ->
            endLifecycleEvent
        }

    /**
     * 自动结束事件流的目标生命周期事件
     * * 默认为onDestroy时结束数据流，可在子类覆盖重写
     * */
    open val endLifecycleEvent : Lifecycle.Event
        get() = Lifecycle.Event.ON_DESTROY

    //<editor-fold desc="LifecycleScopeProvider接口实现">

    /**
     * The observable representing the lifecycle of the [ViewModel].
     *
     * @return [Observable] modelling the [ViewModel] lifecycle.
     */
    override fun lifecycle(): Observable<Lifecycle.Event> {
        return mLifecycleEventSubject.hide()
    }

    /**
     * Returns a [CorrespondingEventsFunction] that maps the
     * current event -> target disposal event.
     *
     * @return function mapping the current event to terminal event.
     */
    override fun correspondingEvents(): CorrespondingEventsFunction<Lifecycle.Event> {
        return correspondingEvents
    }

    /**
     * 最新的生命周期事件
     * @return the last seen lifecycle event, or {@code null} if none. Note that is {@code null} is
     *     returned at subscribe-time, it will be used as a signal to throw a {@link
     *     LifecycleNotStartedException}.
     */
    override fun peekLifecycle(): Lifecycle.Event {
        return mLifecycleEventSubject.value!!
    }

    //</editor-fold>

    //<editor-fold desc="Lifecycle监听">

    /**
     * 外部绑定的生命周期onCreate
     * */
    override fun onCreate() {
        super.onCreate()
        backFillEvents(Lifecycle.State.CREATED)
    }

    /**
     * 外部绑定的生命周期onStart
     * */
    override fun onStart() {
        super.onStart()
        backFillEvents(Lifecycle.State.STARTED)
    }

    /**
     * 外部绑定的生命周期onResume
     * */
    override fun onResume() {
        super.onResume()
        backFillEvents(Lifecycle.State.RESUMED)
    }

    /**
     * 外部绑定的生命周期onPause
     * */
    override fun onPause() {
        super.onPause()
        backFillEvents(Lifecycle.State.STARTED)
    }

    /**
     * 外部绑定的生命周期onStop
     * */
    override fun onStop() {
        super.onStop()
        backFillEvents(Lifecycle.State.CREATED)
    }

    /**
     * 外部绑定的生命周期onDestroy
     * */
    override fun onDestroy() {
        super.onDestroy()
        backFillEvents(Lifecycle.State.DESTROYED)
    }


    /**
     * Backfill if already created for boundary checking. We do a trick here for corresponding events
     * where we pretend something is created upon initialized state so that it assumes the
     * corresponding event is DESTROY.
     */
    protected open fun backFillEvents(lifecycleState : Lifecycle.State) {
        val correspondingEvent = when (lifecycleState) {
            Lifecycle.State.INITIALIZED -> Lifecycle.Event.ON_CREATE
            Lifecycle.State.CREATED -> Lifecycle.Event.ON_START
            Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> Lifecycle.Event.ON_RESUME
            else -> Lifecycle.Event.ON_DESTROY
        }
        mLifecycleEventSubject.onNext(correspondingEvent)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

        if (!(event == Lifecycle.Event.ON_CREATE && mLifecycleEventSubject.value == event)) {
            // Due to the INITIALIZED->ON_CREATE mapping trick we do in backfill(),
            // we fire this conditionally to avoid duplicate CREATE events.
            mLifecycleEventSubject.onNext(event)
        }
    }

    //</editor-fold>
}