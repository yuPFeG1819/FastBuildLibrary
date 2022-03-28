package com.yupfeg.sample

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.multidex.MultiDex
import com.yupfeg.base.tools.ext.initToast
import com.yupfeg.base.tools.process.ProcessLifecycleOwner
import com.yupfeg.logger.ext.logd
import com.yupfeg.logger.ext.setDslLoggerConfig
import com.yupfeg.logger.printer.LogcatPrinter
import com.yupfeg.remote.tools.handler.GlobalHttpResponseProcessor
import com.yupfeg.sample.tools.GlobalResponseHandler
import kotlin.properties.Delegates

class App : Application(){

    companion object{
        var INSTANCE : Application by Delegates.notNull()
            private set

        var appContext : Context by Delegates.notNull()
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appContext = this.applicationContext
        initLoggerConfig()
        initToast()
        initProcessLifecycleOwner()
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

    private fun initProcessLifecycleOwner(){
        ProcessLifecycleOwner.init(this)
        ProcessLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                logd("进入前台")
            }

            override fun onStop(owner: LifecycleOwner) {
                logd("进入后台")
            }
        })
    }

}