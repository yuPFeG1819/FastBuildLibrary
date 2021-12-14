package com.yupfeg.base.tools

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.yupfeg.logger.ext.logd
import java.util.*

/**
 * Activity管理类，用于全局的Activity管理，方便跨页面关闭
 * * 使用object关键字生成ActivityStackHelper单例
 * @author yuPFeG
 * @date 2019/9/8
 */
object ActivityStackHelper : Application.ActivityLifecycleCallbacks{
    private var activityStack: Stack<Activity> = Stack()
    private const val TAG = "AppActivityManager"

    /**
     * 进程内最后一个Activity退出回调
     * * 在真机上，application的`onTerminate`方法可能不会调用
     * * 该函数仅适用于在前台视图关闭后，做一些相关处理
     * */
    interface OnLastActivityRemoveCallBack{
        fun onLastActivityRemoved()
    }

    private var mLastActivityRemoveCallBack : OnLastActivityRemoveCallBack ?= null

    /**
     * 初始化Application下所有activity生命周期的回调监听
     * @param application
     * @param lastRemoveCallBack 最后一个Activity移除后的回调函数
     */
    @JvmStatic
    fun initActivityLifecycleListener(
        application: Application,
        lastRemoveCallBack : OnLastActivityRemoveCallBack?= null
    ){
        application.registerActivityLifecycleCallbacks(this)
        mLastActivityRemoveCallBack = lastRemoveCallBack
    }

    /**
     * 销毁Application下所有Activity生命周期的回调监听
     * */
    @JvmStatic fun destroyActivityLifeListener(application: Application){
        application.unregisterActivityLifecycleCallbacks(this)
    }

    //<editor-fold desc="Activity生命周期监听实现">

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //添加到activity管理堆栈
        addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {
        //移除堆栈中指定的Activity
        removeActivityForStack(activity)

        if (getActivityStackSize() <= 0){
            //进程的最后一个Activity已结束
            mLastActivityRemoveCallBack?.onLastActivityRemoved()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    //</editor-fold desc="Activity生命周期监听实现">

    //<editor-fold desc="Activity堆栈处理">

    /**
     * 获得activity堆栈实例
     * @return
     */
    @Suppress("unused")
    private fun getActivityStack() = activityStack

    /**获取当前activity堆栈大小*/
    @Suppress("unused")
    @JvmStatic fun getActivityStackSize() : Int = activityStack.size


    /**
     * 从堆栈中删除指定activity
     * @param activity activity实例
     */
    @JvmStatic fun removeActivityForStack(activity: Activity) {
        if (isActivityExist(activity.javaClass)) {
            activityStack.remove(activity)
        }

    }

    /**
     * 判断堆栈中是否存在指定类
     */
    @JvmStatic fun isActivityExist(cls: Class<*>): Boolean {
        for (activity in activityStack) {
            activity?.let {
                if (it.javaClass == cls) {
                    return true
                }
            }
        }
        return false
    }


    /**
     * 添加Activity到堆栈
     * @JvmStatic 声明能够在java调用该static方法
     *
     */
    @JvmStatic fun addActivity(activity: Activity) {
        activityStack.add(activity)
        logd(TAG,"activity stack add $activity")
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    @JvmStatic fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 获取上一次添加的Activity(堆栈中倒数第二个压入的)
     * @return 如果堆栈只有一个或者没有数据，则返回null
     */
    @Suppress("unused")
    fun getPreAddActivity() : Activity?{
        if (activityStack.size in 0..1) return null
        return activityStack.elementAt(activityStack.size-2)
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    @Suppress("unused")
    @JvmStatic fun finishActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    @Suppress("unused")
    @JvmStatic fun finishActivity(activity: Activity) {
        if (isActivityExist(activity.javaClass)) {
            activityStack.remove(activity)
        }
        if (!activity.isFinishing) {
            logd(TAG,"activity stack finish $activity")
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     * @param cls 需要结束的activity类，例如MainActivity::class.java
     * 或者在java调用则是MainActivity.class
     */
    @Suppress("unused")
    @JvmStatic fun finishActivity(cls: Class<*>) {
        val size = activityStack.size-1
        for (i in size downTo 0){
            val activity = activityStack[i]
            activity?.let {
                if (it.javaClass == cls && !activity.isFinishing) {
                    activity.finish()
                    //删除堆栈中的类对象
                    activityStack.remove(activity)
                    return
                }
            }
        }
    }

    /**
     * 结束指定类名的最早的一个Activity实例
     */
    @Suppress("unused")
    @JvmStatic fun finishActivityByFirst(cls: Class<*>) {
        val size = activityStack.size-1
        for (i in size downTo 0){
            val activity = activityStack[i]
            activity?.let {
                if (it.javaClass == cls && !activity.isFinishing) {
                    activity.finish()
                    //删除堆栈中的类对象
                    activityStack.remove(activity)
                    return
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @Suppress("unused")
    @JvmStatic fun finishAllActivity() {
        var i = 0
        val size = activityStack.size
        while (i < size) {
            val activity = activityStack[i]
            activity?.let {
                if (!it.isFinishing){
                    it.finish()
                }
            }
            i++
        }
        activityStack.clear()
    }

    /**
     * 结束除指定页面的所有Activity
     * @param ignoreClass 指定保留的activity
     */
    @Suppress("unused")
    @JvmStatic fun finishAllActivityIgnoreActivity(ignoreClass: Class<*>){
        val size = activityStack.size-1
        //倒叙遍历堆栈
        for (i in size downTo 0){
            logd(TAG,"activity堆栈遍历，第 $i 项")
            val activity = activityStack[i]
            activity?.let {
                if (!it.isFinishing && it.javaClass != ignoreClass){
                    logd(TAG,"activity堆栈遍历，第 $i 项，非指定忽略项，关闭并删除activity")
                    it.finish()
                    //删除堆栈中的activity实例
                    activityStack.remove(activity)
                }
            }
        }
    }

    //</editor-fold desc="Activity堆栈处理">

}