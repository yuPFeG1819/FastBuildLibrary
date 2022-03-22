package com.yupfeg.base.viewmodel.ext

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yupfeg.base.viewmodel.AppViewModelDelegate
import com.yupfeg.base.viewmodel.ViewModelDelegate

//<editor-fold desc="activity的viewModel委托">
/**
 * [ComponentActivity]的拓展函数，Activity生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 * @param factoryProducer 提供自定义ViewModel创建工厂
 * */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> ComponentActivity.viewModelDelegate(
    noinline factoryProducer : ()-> ViewModelProvider.Factory= {defaultViewModelProviderFactory}
) = ViewModelDelegate(
    clazz = T::class,
    storeProducer = {viewModelStore},
    factoryProducer = factoryProducer
)

/**
 * [ComponentActivity]的拓展函数，获取Application生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 * @param factoryProducer 提供自定义ViewModel创建工厂
 * */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> ComponentActivity.applicationViewModelDelegate(
    noinline factoryProducer : ()-> ViewModelProvider.Factory = {defaultViewModelProviderFactory}
) = AppViewModelDelegate(
    clazz = T::class,
    factoryProducer = factoryProducer
)

//</editor-fold>

//<editor-fold desc="fragment的viewModel委托">

/**
 * [Fragment]的拓展函数，
 * 获取当前Fragment生命周期作用域的ViewModel实例
 * 用于by关键字委托创建viewModel实例
 */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> Fragment.viewModelDelegate(
    noinline factoryProducer : ()-> ViewModelProvider.Factory= {defaultViewModelProviderFactory}
) = ViewModelDelegate(
    clazz = T::class,
    storeProducer = {this.viewModelStore},
    factoryProducer = factoryProducer
)

/**
 * [Fragment]的拓展函数
 * 获取与fragment所在Activity生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> Fragment.activityViewModelDelegate(
    noinline factoryProducer : (()-> ViewModelProvider.Factory)?= null
) = ViewModelDelegate(
    clazz = T::class,
    storeProducer = {requireActivity().viewModelStore},
    factoryProducer = factoryProducer?:{requireActivity().defaultViewModelProviderFactory}
)

/**
 * [Fragment]的拓展函数
 * 获取与Fragment嵌套的Parent Fragment生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> Fragment.parentFragmentViewModelDelegate(
    noinline factoryProducer : (()-> ViewModelProvider.Factory)?= null
) = ViewModelDelegate(
    clazz = T::class,
    storeProducer = { requireParentFragment().viewModelStore },
    factoryProducer = factoryProducer?:{requireParentFragment().defaultViewModelProviderFactory}
)

/**
 * [Fragment]的拓展函数，获取Application生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 * */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> Fragment.applicationViewModelDelegate(
    noinline factoryProducer : ()-> ViewModelProvider.Factory = {defaultViewModelProviderFactory}
) = AppViewModelDelegate(
    clazz = T::class,
    factoryProducer = factoryProducer
)

//</editor-fold>