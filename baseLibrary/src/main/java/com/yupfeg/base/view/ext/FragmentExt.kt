package com.yupfeg.base.view.ext

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.R

// <editor-fold desc="fragment 参数 拓展">

/**
 * [Fragment]的拓展函数，延迟获取[Fragment]创建时的参数
 * - 使用by关键字进行代理
 * @param key 创建时保存在[Bundle]的key值
 * @param default 如果无法获取指定类型的值，则返回默认值
 * */
@Suppress("unused")
inline fun <reified T> Fragment.argumentsLazy(key : String, default : T)
= lazy(LazyThreadSafetyMode.NONE){
    arguments?.get(key) as? T ?: default
}

/**
 * [Fragment]的拓展函数，延迟获取[Fragment]创建时的参数
 * - 使用by关键字进行代理
 * @param key 创建时保存在[Bundle]的key值
 * */
@Suppress("unused")
inline fun <reified T> Fragment.argumentsLazyOrNull(key: String)
= lazy(LazyThreadSafetyMode.NONE){
    arguments?.get(key) as? T
}

// </editor-fold>


// <editor-fold desc="Fragment show hide 拓展">
/**
 * [FragmentActivity]的拓展函数，加载Activity内的多个同级Fragment到同一个ContainerView
 * @param containerViewId 布局id
 * @param showIndex  默认显示的下标
 * @param fragments  加载的fragment的列表
 */
@Deprecated("目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置")
@Suppress("unused")
fun FragmentActivity.addFragmentsToContainerView(@IdRes containerViewId : Int,
                                                 showIndex : Int = 0,
                                                 fragments : List<Fragment>){
    performAddShowHideFragments(
        containerViewId, showIndex,
        supportFragmentManager, fragments
    )
}

/**
 * [FragmentActivity]的拓展方法，在ContainerView内只显示指定Fragment
 * * 调用该方法前，需要先调用[addFragmentsToContainerView]方法来添加fragment
 * 到对应的[FragmentActivity.getSupportFragmentManager]内，否则会报错！
 * @param showFragment 需要显示的fragment
 */
@Deprecated("目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置")
@Suppress("unused")
fun FragmentActivity.showHideFragmentOnContainerView(showFragment: Fragment){
    performShowHideFragments(
        fragmentManager = supportFragmentManager,
        showFragment = showFragment
    )
}

/**
 * [Fragment]的拓展函数，加载Fragment内嵌的多个同级Fragment到同一个ContainerView
 * @param containerViewId 布局id
 * @param showIndex  默认显示的下标
 * @param fragments  加载的fragment的列表
 */
@Deprecated("目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置")
@Suppress("unused")
fun Fragment.addFragmentsToContainerView(
    @IdRes containerViewId : Int,
    showIndex : Int = 0,
    fragments : List<Fragment>
) {
    performAddShowHideFragments(
        containerViewId, showIndex,
        childFragmentManager, fragments
    )
}

/**
 * [Fragment]的拓展方法，在ContainerView内只显示指定Fragment
 * * 调用该方法前，需要先调用[addFragmentsToContainerView]方法来添加fragment
 * 到对应的[Fragment.getChildFragmentManager]内，否则会报错！
 * @param showFragment 需要显示的fragment
 */
@Deprecated("目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置")
@Suppress("unused")
fun Fragment.showHideFragmentOnContainerView(showFragment: Fragment){
    performShowHideFragments(
        fragmentManager = childFragmentManager,
        showFragment = showFragment
    )
}



/**
 * 使用add+show+hide模式加载fragment
 * * TODO 目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置
 * * 默认显示位置[showIndex]的Fragment，最大Lifecycle为[Lifecycle.State.RESUMED]
 * * 其他隐藏的Fragment，最大Lifecycle为[Lifecycle.State.CREATED]
 *
 *@param containerViewId 容器id
 *@param showIndex  需要显示的fragments下标，不处于fragments内，则默认显示第一项
 *@param fragmentManager FragmentManager
 *@param fragments  控制显示的Fragments
 */
@Deprecated("目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置")
private fun performAddShowHideFragments(
    @IdRes containerViewId: Int,
    showIndex: Int,
    fragmentManager: FragmentManager,
    fragments: List<Fragment>
) {
    fragments.takeIf { it.isNotEmpty() }?.also {list->
        val needShowIndex = if (showIndex in list.indices) showIndex else 0
        fragmentManager.beginTransaction().apply {
            for (index in list.indices) {
                val fragment = list[index]
                if (!fragment.isAdded){
                    //设置切换动画
                    setCustomAnimations(
                        R.anim.nav_fragment_enter,
                        R.anim.nav_fragment_exit,
                        R.anim.nav_fragment_pop_enter,
                        R.anim.nav_fragment_pop_exit
                    )
                    add(containerViewId, fragment, fragment.javaClass.name)
                }
                if (needShowIndex == index) {
                    if (!fragment.isVisible){
                        show(fragment)
                    }
                    setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                } else {
                    hide(fragment)
                    setMaxLifecycle(fragment, Lifecycle.State.CREATED)
                }
            }

        }.commit()
    }?: throw IllegalStateException("fragments must not empty")
}

/**
 * 使用show+hide模式控制显示隐藏fragment
 * * TODO 目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置
 * * 显示需要显示的Fragment，最大Lifecycle为[Lifecycle.State.RESUMED]
 * * 其他隐藏的Fragment，最大Lifecycle为[Lifecycle.State.CREATED]
 *
 *@param showFragment  需要显示的fragment，需要该Fragment已经add到[FragmentManager]
 *@param fragmentManager FragmentManager
 */
@Deprecated("目前会导致嵌套fragment内，仅仅只是重置了view,但fragment的变量没有被重置")
private fun performShowHideFragments(
    showFragment : Fragment,
    fragmentManager : FragmentManager
){
    fragmentManager.beginTransaction().apply {
        //获取其中所有的fragment,其他的fragment进行隐藏
        val fragments = fragmentManager.fragments
        if (fragments.contains(showFragment)){
            //设置切换动画
            setCustomAnimations(
                R.anim.nav_fragment_enter,
                R.anim.nav_fragment_exit,
                R.anim.nav_fragment_pop_enter,
                R.anim.nav_fragment_pop_exit
            )
            show(showFragment)
            setMaxLifecycle(showFragment, Lifecycle.State.RESUMED)
            for (fragment in fragments) {
                if (fragment != showFragment) {
                    hide(fragment)
                    setMaxLifecycle(fragment, Lifecycle.State.CREATED)
                }
            }
        }

    }.commit()
}

// </editor-fold desc="Fragment show hide 拓展">

