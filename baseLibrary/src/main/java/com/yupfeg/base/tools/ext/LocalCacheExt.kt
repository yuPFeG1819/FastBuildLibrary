package com.yupfeg.base.tools.ext

import android.app.Application
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVHandler
import com.tencent.mmkv.MMKVLogLevel
import com.tencent.mmkv.MMKVRecoverStrategic
import com.yupfeg.logger.ext.*
import com.yupfeg.logger.ext.logw
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * 本地缓存的工具类Top-Level方法
 * @author yuPFeG
 * @date 2020/10/18
 */

/**本地缓存的日志输出tag常量*/
private const val CACHE_LOG_TAG = "localCache"

// <editor-fold desc="Application初始化缓存">
/**
 * [Application]的拓展函数，初始化本地快速缓存库
 */
@Suppress("unused")
fun Application.initLocalFastCache(){
    MMKV.initialize(this)
    MMKV.registerHandler(object : MMKVHandler {
        override fun onMMKVCRCCheckFail(cacheID: String?): MMKVRecoverStrategic {
            return MMKVRecoverStrategic.OnErrorRecover
        }

        override fun onMMKVFileLengthError(cacheID: String?): MMKVRecoverStrategic {
            return MMKVRecoverStrategic.OnErrorRecover
        }

        override fun wantLogRedirecting(): Boolean {
            //true表示允许log重定向转发
            return true
        }

        override fun mmkvLog(
            logLevel: MMKVLogLevel?, fileName: String?,
            line: Int, func: String?, message: String?
        ) {
            logLevel?:return
            val log = "< ${fileName?:""} : Line $line :: ${func?:""} > $message"
            when(logLevel){
                MMKVLogLevel.LevelNone-> logv(CACHE_LOG_TAG,log)
                MMKVLogLevel.LevelDebug -> logd(CACHE_LOG_TAG,log)
                MMKVLogLevel.LevelInfo -> logi(CACHE_LOG_TAG,log)
                MMKVLogLevel.LevelWarning -> logw(CACHE_LOG_TAG,log)
                MMKVLogLevel.LevelError -> loge(CACHE_LOG_TAG,log)
            }
        }

    })
}

/**
 * [Application]的拓展函数，取消[MMKV]日志的转发
 */
@Suppress("unused")
fun Application.unRegisterFastCacheLog(){
    MMKV.unregisterHandler()
}

// </editor-fold>

// <editor-fold desc="写入读取数据">

/**
 * 使用属性委托方式，从本地缓存读取与写入[Int]类型数据
 * * 在外部通过by关键字实现属性委托
 * @param key 缓存字段key
 * @param defValue 缓存字段对应的默认[Int]数据
 * @param cacheID 缓存区分id,默认为null,缓存在同一个文件内.如果需要根据业务模块区分，则传入不为空的字符串
 * */
@Suppress("unused")
fun localCacheInt(
    key: String? = null, defValue: Int = 0,
    cacheID: String? = null
): ReadWriteProperty<Any, Int> {
    //实际为单例，创建的只是壳对象
    val cacheInstance = if (cacheID?.isNotEmpty() == true){
        MMKV.mmkvWithID(cacheID)
    }else{
        MMKV.defaultMMKV()
    }
    return cacheInstance?.delegate(
        key, defValue, getter = MMKV::getInt, setter = MMKV::encode
    )?:run{
        throw IllegalArgumentException("You should Call MMKV.initialize() first.")
    }
}
/**
 * 使用属性委托方式，从本地缓存读取与写入[Long]类型数据
 * * 在外部通过by关键字实现属性委托
 * *
 * @param key 缓存字段key
 * @param defValue 缓存字段对应的默认[Long]数据
 * @param cacheID 缓存区分id,默认为null,缓存在同一个文件内.如果需要根据业务模块区分，则传入不为空的字符串
 * */
@Suppress("unused")
fun localCacheLong(key: String? = null, defValue: Long = 0,
                   cacheID: String? = null): ReadWriteProperty<Any, Long> {
    //实际为单例，创建的只是壳对象
    val cacheInstance = if (cacheID?.isNotEmpty() == true){
        MMKV.mmkvWithID(cacheID)
    }else{
        MMKV.defaultMMKV()
    }
    return cacheInstance?.delegate(
        key, defValue, getter = MMKV::getLong, setter = MMKV::encode
    )?:run{
        throw IllegalArgumentException("You should Call MMKV.initialize() first.")
    }
}
/**
 * 使用属性委托方式，从本地缓存读取与写入[Float]数据
 * * 在外部通过by关键字实现属性委托
 * @param key 缓存字段key
 * @param defValue 缓存字段对应的默认[Float]数据
 * @param cacheID 缓存区分id,默认为null,缓存在同一个文件内.如果需要根据业务模块区分，则传入不为空的字符串
 *
 * */
@Suppress("unused")
fun localCacheFloat(key: String? = null, defValue: Float = 0f,
                    cacheID: String? = null): ReadWriteProperty<Any, Float> {
    //实际为单例，创建的只是壳对象
    val cacheInstance = if (cacheID?.isNotEmpty() == true){
        MMKV.mmkvWithID(cacheID)
    }else{
        MMKV.defaultMMKV()
    }
    return cacheInstance?.delegate(
        key,defValue,getter = MMKV::getFloat,setter = MMKV::encode
    )?:run{
        throw IllegalArgumentException("You should Call MMKV.initialize() first.")
    }
}
/**
 * 使用属性委托方式，从本地缓存读取与写入[Boolean]数据
 * * 在外部通过by关键字实现属性委托
 * @param key 缓存字段key
 * @param defValue 索引字段对应的默认[Boolean]数据
 * @param cacheID 缓存区分id,默认为null,缓存在同一个文件内.如果需要根据业务模块区分，则传入不为空的字符串
 * */
@Suppress("unused")
fun localCacheBoolean(key: String? = null, defValue: Boolean = false,
                      cacheID: String? = null): ReadWriteProperty<Any, Boolean> {
    //实际为单例，创建的只是壳对象
    val cacheInstance = if (cacheID?.isNotEmpty() == true){
        MMKV.mmkvWithID(cacheID)
    }else{
        MMKV.defaultMMKV()
    }
    return cacheInstance?.delegate(
        key,defValue,getter = MMKV::getBoolean,setter = MMKV::encode
    )?:run{
        throw IllegalArgumentException("You should Call MMKV.initialize() first.")
    }
}

/**
 * 使用属性委托方式，从本地缓存读取与写入[String]数据
 * * 在外部通过by关键字实现属性委托
 * @param key 缓存字段key
 * @param defValue 索引字段对应的默认[String]数据
 * @param cacheID 缓存区分id,默认为null,缓存在同一个文件内.如果需要根据业务模块区分，则传入不为空的字符串
 * */
@Suppress("unused")
fun localCacheString(key: String? = null, defValue: String = "",
                     cacheID: String? = null): ReadWriteProperty<Any, String> {
    //实际为单例，创建的只是壳对象
    val cacheInstance = if (cacheID?.isNotEmpty() == true){
        MMKV.mmkvWithID(cacheID)
    }else{
        MMKV.defaultMMKV()
    }
    return cacheInstance?.delegate(
        key,defValue,getter = {_,_->
            getString(key, defValue)?:defValue
        },setter = MMKV::encode
    )?:run{
        throw IllegalArgumentException("You should Call MMKV.initialize() first.")
    }
}

/**
 * 使用属性委托方式，从本地缓存读取与写入`Set<String>`数据
 * * 在外部通过by关键字实现属性委托
 * @param defValue 缓存字段对应`Set<String>`数据，默认为空的`Set`集合
 * */
@Suppress("unused")
fun localCacheStringSet(key: String?,
                        defValue: Set<String> = emptySet(),
                        cacheID: String? = null) : ReadWriteProperty<Any, Set<String>> {
    //实际为单例，创建的只是壳对象
    val cacheInstance = if (cacheID?.isNotEmpty() == true){
        MMKV.mmkvWithID(cacheID)
    }else{
        MMKV.defaultMMKV()
    }

    return cacheInstance?.delegate(
        key,defValue,getter = {_,_->
            getStringSet(key, defValue)?:defValue
        },setter = MMKV::encode
    )?:run{
        throw IllegalArgumentException("You should Call MMKV.initialize() first.")
    }
}

/**[MMKV]的拓展函数，使用kotlin属性委托方式，委托存值与取值操作*/
private inline fun <T> MMKV.delegate(
    key: String? = null,
    defaultValue: T,
    crossinline getter: MMKV.(String, T) -> T,
    crossinline setter: MMKV.(String, T) -> Unit
): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return if (key.isNullOrEmpty()) {
                defaultValue
            } else {
                getter(key, defaultValue)
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            key ?: return
            setter(key, value)
        }

    }
}

// </editor-fold>