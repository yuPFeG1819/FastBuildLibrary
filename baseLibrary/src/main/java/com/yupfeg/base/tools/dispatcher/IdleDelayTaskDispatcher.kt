package com.yupfeg.base.tools.dispatcher

import android.os.Looper
import android.os.MessageQueue
import androidx.annotation.MainThread
import java.util.*

/**
 * 延迟执行任务的调度器
 * * 仅限于在主线程使用
 * @author yuPFeG
 * @date 2021/12/11
 */
@Suppress("unused")
class IdleDelayTaskDispatcher {
    private var mTaskQueue : Queue<ITask> = LinkedList()

    private val mIdleHandler = MessageQueue.IdleHandler {
        // 分批执行的好处在于每一个task占用主线程的时间相对
        // 来说很短暂，并且此时CPU是空闲的，这些能更有效地避免UI卡顿
        mTaskQueue.poll()?.also {task->
            task.run()
        }
        !mTaskQueue.isEmpty()    //如果所有延迟启动任务都结束，则移除该IdleHandler
    }

    /**
     * 添加延迟任务
     * */
    fun addTask(task : ITask) : IdleDelayTaskDispatcher{
        mTaskQueue.offer(task)
        return this
    }

    /**
     * 开启执行延迟任务
     * */
    @MainThread
    fun start(){
        Looper.myQueue().addIdleHandler(mIdleHandler)
    }

    /**
     * 清空所有延迟任务
     * */
    fun clearTask(){
        mTaskQueue.clear()
    }
}