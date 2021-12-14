package com.yupfeg.base.domain.extra

/**
 * 业务用例内部单次事件的回收功能
 * * 推荐仅面向`UseCase`实现该接口
 * @author yuPFeG
 * @date 2021/04/13
 */
@Suppress("unused")
interface EventReleasable {

    /**
     * 回收事件资源，避免内存泄漏
     * * 推荐在ViewModel的`onClear`调用，避免在视图生命周期回收，造成遗漏事件变化
     * */
    fun releaseEvent()
}