package com.yupfeg.base.application

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.properties.Delegates

/**
 * 基类application
 * * 继承自[MultiDexApplication]
 * * 内部维护一个Application范围的[ViewModelStore]，
 * 可以此来借助 Application 来管理一个应用级 的 SharedViewModel，
 * 实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
 * @author yuPFeG
 * @date 2019/9/19
 */
open class BaseApplication : Application(), ViewModelStoreOwner{

    private lateinit var mAppViewModelStore: ViewModelStore

    /**
     * App范围协程作用域
     * * 不需要取消这个作用域，因为它会随着进程结束而结束
     * */
    @Suppress("unused")
    val applicationScope : CoroutineScope
        get() = mApplicationScope ?: createApplicationScope()
    private var mApplicationScope : CoroutineScope ?= null

    companion object{
        lateinit var INSTANCE : BaseApplication

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
        mAppViewModelStore = ViewModelStore()
    }

    /**ViewModelStoreOwner接口方法实现，表示其作用域范围在整个Application*/
    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

    /**
     * 创建App范围的协程作用域
     * 通常用于不跟随UI视图的后台协程任务使用
     * */
    protected open fun createApplicationScope() : CoroutineScope{
        //使用SupervisorJob，不会让作用域内的协程出现异常后，影响整个作用域内所有协程的运行
        return CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            .also { mApplicationScope = it }
    }

}