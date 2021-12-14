package com.yupfeg.base.data.dto

/**
 * 数据层的返回状态
 * * 将数据层数据映射成对应的domain层状态
 * @author yuPFeG
 * @date 2020/02/18
 */
sealed class DataStateResult<out T> {
    companion object Factory{
        /**切换到获取数据成功状态*/
        fun <T> success(result: T): DataStateResult<T> = Success(result)
        /**切换到默认状态*/
        fun <T> idle(): DataStateResult<T> = Idle
        /**切换至正在加载状态*/
        fun <T> loading(): DataStateResult<T> = Loading
        /**切换加载失败状态*/
        fun <T> failure(error: Throwable): DataStateResult<T> = Failure(error)
    }

    /**恢复到默认状态*/
    object Idle : DataStateResult<Nothing>()
    /**正在加载状态*/
    object Loading : DataStateResult<Nothing>()
    /**加载失败状态*/
    data class Failure(val error: Throwable) : DataStateResult<Nothing>()
    /**获取数据成功状态*/
    data class Success<out T>(val data: T) : DataStateResult<T>()
}