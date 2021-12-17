package com.yupfeg.sample

import com.yupfeg.base.application.BaseApplication
import com.yupfeg.base.tools.dispatcher.ITask
import com.yupfeg.base.tools.dispatcher.IdleDelayTaskDispatcher
import com.yupfeg.base.tools.initToast
import com.yupfeg.base.tools.pool.WebViewPool
import com.yupfeg.logger.ext.setDslLoggerConfig
import com.yupfeg.logger.printer.LogcatPrinter

class App : BaseApplication(){

    override fun onCreate() {
        super.onCreate()
        initToast()
        initLoggerConfig()
        val dispatcher = IdleDelayTaskDispatcher().addTask(object : ITask {
            override fun run() {
                WebViewPool.prepare(this@App.applicationContext)
            }
        })
        dispatcher.start()
    }

    /**
     * 初始化日志管理器
     * */
    private fun initLoggerConfig(){
        setDslLoggerConfig {
            //开启调用位置追踪
            isDisplayClassInfo = true
            //添加线上捕获异常的日志输出
            logPrinters = listOf(LogcatPrinter(enable = BuildConfig.DEBUG))
        }
    }
}