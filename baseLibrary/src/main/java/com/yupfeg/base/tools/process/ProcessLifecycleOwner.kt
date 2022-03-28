package com.yupfeg.base.tools.process

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * 为整个应用程序进程提供生命周期的类。
 * * 可以将此 LifecycleOwner 视为所有应用活动的组合。
 *   但`Lifecycle.Event.ON_CREATE`将只被调度一次，而`Lifecycle.Event.ON_DESTROY`将永远不会被调度。
 * * 参考`jetpack lifecycle-process`库，封装一个全局Application的生命周期
 * * 整合进全局的Activity栈，方便快捷操作Activity
 * * 整合整个应用范围的协程作用域，替代`GlobalScope`
 * * 在整个应用程序中，推荐只创建一次，在多进程情况下，只需要检测一次初始化一次即可。
 * @author yuPFeG
 * @date 2022/03/28
 */
object ProcessLifecycleOwner : LifecycleOwner, ViewModelStoreOwner {

    /**
     * 确认应用进入后台的超时时间，默认为700ms，
     * - 确保能够过滤屏幕旋转导致的Activity重建
     * */
    private const val TIMEOUT_MS : Long = 700

    /**提供全局进程范围的ViewModel作用域*/
    private var mAppViewModelStore : ViewModelStore? = null

    /**
     * activity缓存管理栈
     * - 方便移除结束指定Activity
     * */
    @JvmField
    val activityStack : ActivityStackHelper = ActivityStackHelper()

    /**
     * Lifecycle事件分发器
     * */
    private var mRegistry : LifecycleRegistry? = null

    /**
     * 当前进入onStart的活动计数器
     * - 处于onStart时+1，处于onStop时-1，同一时刻有且只有1个前台Activity
     * */
    private var mStartedCounter = 0
    /**
     * 当前进入onResume的视图计数器
     * - 处于onResume时+1，处于onPause时-1，同一时刻有且只有1个前台Activity
     * */
    private var mResumedCounter = 0

    /**
     * 是否已分发stop事件
     * - 必须要初始为true，在第一次启动时将onStart生命周期分发，
     * 否则第一次进入后台时，onPause分发时会先分发最小的生命周期，
     * 参见[LifecycleRegistry.ObserverWithState.dispatchEvent]的逻辑
     * */
    private var mSentStopEvent : Boolean = true

    /**
     * 是否已分发pause事件
     * - 必须要初始为true，在第一次启动时将OnResume生命周期分发，
     * */
    private var mSentPauseEvent : Boolean = true

    private var mHandler : Handler = Handler(Looper.getMainLooper())

    /**
     * App范围协程作用域
     * * 不需要取消这个作用域，因为它会随着进程结束而结束
     * */
    @Suppress("unused")
    val applicationScope : CoroutineScope
        get() = mApplicationScope ?: createApplicationScope()
    private var mApplicationScope : CoroutineScope?= null

    /**
     * 等待程序进入后台的延迟任务
     * - 需要延迟足够长时间，以确保由于配置更改等操作重建 activity 时不会分发任何事件
     * */
    private val mBackgroundDelayTask : Runnable = Runnable {
        //已确定进入后台，开始分发事件
        dispatchPauseIfNeeded()
        dispatchStopIfNeeded()
    }

    override fun getLifecycle(): Lifecycle {
        return mRegistry
            ?:throw NullPointerException("you should init ProcessLifecycleOwner on Application")
    }

    /**
     * 提供ViewModel作用域
     * - 相当于全局单例，生命周期跟随应用进程
     * - 可以此来借助 Application 来管理一个应用级 的 SharedViewModel，
     * 实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
     * */
    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
            ?:throw NullPointerException("you should init ProcessLifecycleOwner on Application")
    }

    /**
     * 校验当前应用程序是否处于前台
     * */
    @Suppress("unused")
    val isForeground : Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    /**
     * 在`Application`初始化，或者在`ContentProvider`初始化
     * @param context
     * */
    @JvmStatic
    fun init(context: Context){
        attach(context)
    }

    /**
     * 绑定应用生命周期
     * @param context
     * */
    private fun attach(context : Context){
        mRegistry = LifecycleRegistry(this)
        mAppViewModelStore = ViewModelStore()
        //分发onCreate事件，正常有且只会分发一次
        mRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        val app = (context.applicationContext as? Application)
        app?.registerActivityLifecycleCallbacks(object : DefaultActivityLifecycleCallback(){

            @SuppressLint("RestrictedApi")
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                //添加到activity管理栈
                activityStack.addActivityToStack(activity)
            }

            override fun onActivityPostStarted(activity: Activity) {
                doOnActivityStart()
            }

            override fun onActivityPostResumed(activity: Activity) {
                doOnActivityResume()
            }

            override fun onActivityPaused(activity: Activity) {
                doOnActivityPause()
            }

            override fun onActivityStopped(activity: Activity) {
                doOnActivityStop()
            }

            override fun onActivityDestroyed(activity: Activity) {
                //移除activity栈中的对应元素
                activityStack.removeActivityForStack(activity)
            }

        })
    }

    /**
     * Activity进入onStart事件
     * */
    private fun doOnActivityStart(){
        mStartedCounter++
        if (mStartedCounter == 1 && mSentStopEvent){
            //应用第一次启动或从后台重新进入，分发onStart事件
            mRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
            mSentStopEvent = false
        }
    }

    /**
     * Activity进入onResume事件
     * */
    private fun doOnActivityResume(){
        mResumedCounter++
        if (mResumedCounter == 1){
            if (mSentPauseEvent){
                //应用第一次启动或从后台重新进入，分发onResume事件
                mRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                mSentPauseEvent = false
            }else{
                //还处于等待进入后台阶段，移除正在等待的延迟任务
                mHandler.removeCallbacks(mBackgroundDelayTask)
            }
        }
    }

    /**
     * Activity进入onPause事件
     * */
    private fun doOnActivityPause(){
        mResumedCounter--
        if (mResumedCounter == 0){
            //当前已没有活跃的视图，进入延迟等待进入后台阶段
            mHandler.postDelayed(mBackgroundDelayTask,TIMEOUT_MS)
        }
    }

    /**
     * Activity进入onStop事件
     * */
    private fun doOnActivityStop(){
        mStartedCounter--
        dispatchStopIfNeeded()
    }

    /**
     * 分发应用程序处于后台的事件，此时代表应用已进入后台
     * */
    private fun dispatchPauseIfNeeded(){
        if (mResumedCounter == 0){
            //等待超时后依然没有视图处于前台，则分发onPause
            mSentPauseEvent = true
            mRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    /**
     * 分发应用程序处于后台的事件，此时代表应用已进入后台
     * */
    private fun dispatchStopIfNeeded(){
        if (mStartedCounter == 0 && mSentPauseEvent){
            //视图没有处于前台，并且已分发了onPause
            mRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            mSentStopEvent = true
        }
    }

    /**
     * 创建App范围的协程作用域
     * 通常用于不跟随UI视图的后台协程任务使用
     * - 用于替代GlobalScope
     * */
    private fun createApplicationScope() : CoroutineScope{
        //使用SupervisorJob，不会让作用域内的协程出现异常后，影响整个作用域内所有协程的运行
        return CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            .also { mApplicationScope = it }
    }

}

private open class DefaultActivityLifecycleCallback : Application.ActivityLifecycleCallbacks{
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}