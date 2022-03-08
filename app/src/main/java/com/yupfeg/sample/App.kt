package com.yupfeg.sample

import com.yupfeg.base.application.BaseApplication
import com.yupfeg.base.tools.ext.initToast
import com.yupfeg.logger.ext.setDslLoggerConfig
import com.yupfeg.logger.printer.LogcatPrinter
import com.yupfeg.remote.tools.handler.GlobalHttpResponseProcessor
import com.yupfeg.sample.tools.GlobalResponseHandler

class App : BaseApplication(){

    override fun onCreate() {
        super.onCreate()
        initLoggerConfig()
        initToast()

        //设置全局http响应
        GlobalHttpResponseProcessor.setResponseHandler(GlobalResponseHandler())
    }

    /**
     * 初始化日志管理器
     * */
    private fun initLoggerConfig(){
        setDslLoggerConfig {
            //开启调用位置追踪
            isDisplayClassInfo = true
            //添加线上捕获异常的日志输出
            logPrinters = listOf(LogcatPrinter(enable = true))
        }
    }

}