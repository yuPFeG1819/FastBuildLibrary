package com.yupfeg.base.tools.dispatcher

/**
 * 抽象化的任务流程，应用于页面启用等位置，进行延迟或异步启动优化
 * @author yuPFeG
 * @date 2021/12/11
 */
interface ITask {
    /**任务执行操作*/
    fun run()
}