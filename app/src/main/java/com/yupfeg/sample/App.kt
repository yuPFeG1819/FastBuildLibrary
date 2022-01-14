package com.yupfeg.sample

import com.yupfeg.base.application.BaseApplication
import com.yupfeg.base.tools.ext.initToast
import com.yupfeg.logger.ext.setDslLoggerConfig
import com.yupfeg.logger.printer.LogcatPrinter

class App : BaseApplication(){

    override fun onCreate() {
        super.onCreate()
        initLoggerConfig()
        initToast()
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