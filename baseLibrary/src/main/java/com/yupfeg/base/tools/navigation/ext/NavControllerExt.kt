package com.yupfeg.base.tools.navigation.ext

import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.yupfeg.base.tools.navigation.NavControllerMediator

/**
 * [ComponentActivity]的拓展函数，委托获取[View]在`Activity`中对应的[NavController]
 * @param view 关联了Navigation的view
 */
@Suppress("unused")
fun ComponentActivity.navControllerDelegate(view: View)
    = NavControllerMediator(view,this.lifecycle)

/**
 * [ComponentActivity]的拓展函数，委托获取`viewId`在`Activity`中对应的[NavController]
 * @param viewId 关联了Navigation的viewId
 */
@Suppress("unused")
fun ComponentActivity.navControllerDelegate(viewId : Int)
    = NavControllerMediator(viewId,this.lifecycle)

/**
 * [Fragment]的拓展函数，委托获取在`Fragment`中对应的[NavController]
 * */
@Suppress("unused")
fun Fragment.navControllerDelegate()
    = NavControllerMediator(this.lifecycle)