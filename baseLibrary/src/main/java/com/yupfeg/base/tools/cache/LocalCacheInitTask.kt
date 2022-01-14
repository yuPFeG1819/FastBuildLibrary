package com.yupfeg.base.tools.cache

import com.yupfeg.dispatcher.task.Task

/**
 * 初始化本地Key-Value缓存的启动任务
 * - 需要配合`StartTaskDispatcher`进行启动任务调度
 * @author yuPFeG
 * @date 2022/01/13
 */
@Suppress("unused")
class LocalCacheInitTask : Task(){
    companion object{
        const val TAG = "InitLocalCacheTask"
    }

    override val tag: String
        get() = TAG

    override val isOnlyMainProcess: Boolean
        get() = true

    override fun run() {
        initLocalFastCache(context)
    }
}