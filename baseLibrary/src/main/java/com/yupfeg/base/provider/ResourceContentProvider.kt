package com.yupfeg.base.provider

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.yupfeg.logger.ext.logd

/**
 * 仅用作获取本地资源的提供类
 * * 同时提供`Application`级的context
 * * 在Application初始化之前执行，使用时注意多进程问题
 *
 * @author yuPFeG
 * @date 2020/10/26
 */
class ResourceContentProvider : ContentProvider(){

    companion object{
        private lateinit var mInstance : ResourceContentProvider
        private lateinit var mApplicationContext: Context

        @JvmStatic val applicationContext : Context
            get() = mApplicationContext

        @JvmStatic fun getInstance() : ResourceContentProvider{
            return mInstance
        }
    }

    override fun onCreate(): Boolean {
        logd("ResourceContentProvider init ")
        mInstance = this
        mApplicationContext = this.context!!
        return true
    }

    //-------------父类继承方法（皆为空实现）START--------------

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
       return 0
    }

    //-------------父类继承方法（皆为空实现）END --------------

    /**
     * 获取本地字符串
     * @param stringRes
     * */

    fun getString(@StringRes stringRes : Int) = context!!.getString(stringRes)

    /**
     * 获取本地字符串数组
     * @param stringArrayRes
     */
    @Suppress("unused")
    fun getStringArray(@ArrayRes stringArrayRes : Int) : Array<out String> {
        return context!!.resources.getStringArray(stringArrayRes)
    }

    /**
     * 获取本地颜色值
     * @param colorRes
     * */
    @ColorInt
    fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context!!,colorRes)


    /**
     * 获取本地drawable资源
     * @param drawableRes
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(@DrawableRes drawableRes : Int)  = ContextCompat.getDrawable(context!!,drawableRes)
}