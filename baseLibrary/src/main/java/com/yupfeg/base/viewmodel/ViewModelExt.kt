package com.yupfeg.base.viewmodel

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * [ComponentActivity]的拓展函数，获取Application生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 * @param factoryProducer 提供自定义ViewModel创建工厂
 * */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> ComponentActivity.applicationViewModels(
    noinline factoryProducer : ()-> ViewModelProvider.Factory = {defaultViewModelProviderFactory}
) = AppViewModelDelegate(
    clazz = T::class,
    factoryProducer = factoryProducer
)

/**
 * [Fragment]的拓展函数，获取与Fragment嵌套的Parent Fragment生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 */
@Suppress("unused")
@MainThread
inline fun <reified VM : ViewModel> Fragment.parentFragmentViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = createViewModelLazy(
    VM::class, { requireParentFragment().viewModelStore },
    factoryProducer ?: { requireParentFragment().defaultViewModelProviderFactory }
)

/**
 * [Fragment]的拓展函数，获取Application生命周期作用域的ViewModel
 * * 用于by关键字委托创建viewModel实例
 * */
@Suppress("unused")
@MainThread
inline fun <reified T : ViewModel> Fragment.applicationViewModels(
    noinline factoryProducer : ()-> ViewModelProvider.Factory = {defaultViewModelProviderFactory}
) = AppViewModelDelegate(
    clazz = T::class,
    factoryProducer = factoryProducer
)
