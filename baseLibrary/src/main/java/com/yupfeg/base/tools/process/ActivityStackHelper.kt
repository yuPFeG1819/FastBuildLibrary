package com.yupfeg.base.tools.process

import android.app.Activity
import com.yupfeg.base.tools.ArrayStack

/**
 * Activity管理类，用于全局的Activity管理，方便跨页面关闭视图
 * * 使用object关键字生成ActivityStackHelper单例
 * @author yuPFeG
 * @date 2019/9/8
 */
class ActivityStackHelper {
    private var mActivityStack: ArrayStack<Activity> = ArrayStack()

    /**获取当前activity堆栈大小*/
    @Suppress("unused")
    val stackSize : Int = mActivityStack.size

    /**
     * 添加Activity到任务栈
     * - 只允许内部调用，避免外部不受控制的添加栈元素
     * @JvmStatic 声明能够在java调用该static方法
     */
    internal fun addActivityToStack(activity: Activity) {
        mActivityStack.push(activity)
    }

    /**
     * 从堆栈中删除指定activity
     * - 正常情况下，都是移除倒数第2个Activity
     * - 只允许内部调用，避免外部不受控制的删除栈元素
     * @param activity activity实例
     */
    internal fun removeActivityForStack(activity: Activity) {
        mActivityStack.remove(activity)
    }


    /**
     * 判断堆栈中是否存在指定Activity类
     * @param cls 指定Activity的类型
     */
    @Suppress("unused")
    fun isActivityExist(cls: Class<*>): Boolean {
        for (activity in mActivityStack) {
            if (activity.javaClass == cls) return true
        }
        return false
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    @Suppress("unused")
    fun currentActivity(): Activity {
        return mActivityStack.peekLast() ?:throw NullPointerException("activity stack is null")
    }

    /**
     * 获取上一次添加的Activity(堆栈中倒数第二个压入的)
     * @return 如果堆栈只有一个或者没有数据，则返回null
     */
    @Suppress("unused")
    fun getPreAddActivity() : Activity?{
        if (mActivityStack.size in 0..1) return null
        return mActivityStack.elementAtOrElse(mActivityStack.size-2){
            throw NullPointerException("activity stack cant get pre add activity")
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    @Suppress("unused")
    fun finishActivity() {
        val activity = mActivityStack.pop()
        activity?:return
        activity.finish()
    }

    /**
     * 结束指定类名的Activity
     * @param clazz 需要结束的activity类，例如MainActivity::class.java
     * 或者在java调用则是MainActivity.class
     */
    @Suppress("unused")
    fun finishActivity(clazz: Class<*>) {
        val iterable = mActivityStack.iterator()
        while (iterable.hasNext()){
            val activity = iterable.next()
            if (!activity.isFinishing && activity.javaClass == clazz){
                activity.finish()
                //直接操作迭代器中移除，避免重复的遍历检索
                iterable.remove()
            }
        }
    }

    /**
     * 结束指定类名的最早的一个Activity实例
     */
    @Suppress("unused")
    fun finishActivityFirst(clazz: Class<*>) {
        val iterable = mActivityStack.iterator()
        while (iterable.hasNext()){
            val activity = iterable.next()
            if (!activity.isFinishing && activity.javaClass == clazz){
                activity.finish()
                //直接操作迭代器中移除，避免重复的遍历检索
                iterable.remove()
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @Suppress("unused")
    fun finishAllActivity() {
        while (mActivityStack.isNotEmpty){
            val activity = mActivityStack.pop()
            activity?:continue
            if (!activity.isFinishing){
                activity.finish()
            }
        }
    }

    /**
     * 结束除指定页面的所有Activity
     * @param ignoreClass 指定保留的activity
     */
    @Suppress("unused")
    fun finishAllActivityIgnoreActivity(ignoreClass: Class<*>){
        //任务栈迭代器，从最新进入的Activity开始遍历
        val iterable = mActivityStack.iterator()
        while (iterable.hasNext()){
            val activity = iterable.next()
            if (!activity.isFinishing && activity.javaClass != ignoreClass){
                activity.finish()
                //直接从迭代器中移除这个操作，避免重复的遍历检索
                iterable.remove()
            }
        }
    }

}