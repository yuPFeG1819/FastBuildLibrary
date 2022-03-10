package com.yupfeg.base.tools.databinding.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.reflect.Method

/**
 * 创建layoutId对应的DataBinding类实例
 * @param inflater
 * @param layoutId 布局id
 * @param container
 * @return 如果指定视图已绑定，则直接返回对应实例，否则创建新实例
 * */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> requireBindingInflate(
    inflater: LayoutInflater, @LayoutRes layoutId: Int, container: ViewGroup?
): T = requireNotNull(DataBindingUtil.inflate(inflater, layoutId, container, false)){
    "cannot find the matched layout."
}

/**
 * 绑定根View，获取视图对应的DataBinding实例
 * @param view 根视图view
 * @return 如果指定视图已绑定，则直接返回对应实例，否则绑定后创建实例
 * */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> requireBindingView(view: View): T =
    requireNotNull(DataBindingUtil.bind(view)) { "cannot find the matched layout." }

const val DATA_BINDING_INFLATE_NAME = "inflate"
const val DATA_BINDING_BIND_NAME = "bind"

/**
 * 反射获取自动生成DataBinding类内部的`inflate`方法信息
 * - 在build/generated/data_binding_base_class_source_out目录下查看对应生成的文件
 * */
fun <T : ViewDataBinding> Class<T>.getInflateMethod() : Method
    = this.getMethod(DATA_BINDING_INFLATE_NAME,LayoutInflater::class.java)

/**
 * 反射获取自动生成DataBinding类内部的`bind`方法信息
 * - 在build/generated/data_binding_base_class_source_out目录下查看对应生成的文件
 * */
@Suppress("unused")
fun <T : ViewDataBinding> Class<T>.getBindMethod() : Method
    = this.getMethod(DATA_BINDING_BIND_NAME,View::class.java)

