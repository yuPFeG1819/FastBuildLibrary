package com.yupfeg.base.view.ext

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.R

/**
 * AndroidX下的Fragment相关拓展方法提取文件
 * @author yuPFeG
 * @date 2020/08/05
 */

// <editor-fold desc="Fragment show hide 拓展">
/**
 * [FragmentActivity]的拓展函数，加载Activity内的多个同级Fragment到同一个ContainerView
 * @param containerViewId 布局id
 * @param showIndex  默认显示的下标
 * @param fragments  加载的fragment的列表
 */
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

// <editor-fold desc="Fragment的Activity跳转拓展">

/**
 * 使用预设的页面启动动画，启动跳转activity
 * @param clazz 目标activity
 * @param bundle 跳转传递值
 * @param animType 跳转动画类型 [TransitionAnimType]，默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Suppress("unused")
fun Fragment.startActivityWithAnim(
    clazz: Class<*>, bundle: Bundle?,
    animType: TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    requireActivity().apply {
        val intent = Intent(this, clazz)
        bundle?.let { intent.putExtras(bundle) }
        startActivity(intent)
        performTransitionAnim(animType)
    }
}

/**
 * 使用预设的页面启动动画，启动跳转activity并设置返回请求码
 * @param clazz 目标activity
 * @param bundle 跳转传递值
 * @param requestCode   返回时请求码
 * @param animType 跳转动画类型 [TransitionAnimType],默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Suppress("unused")
fun Fragment.startActivityWithAnimForResult(
    clazz: Class<*>, bundle: Bundle?, requestCode: Int,
    animType: TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    val intent = Intent(requireActivity(), clazz)
    bundle?.let { intent.putExtras(bundle) }
    startActivityWithAnimForResult(intent, requestCode, animType)
}

/**
 * 使用预设的页面启动动画，启动跳转activity并设置返回请求码
 * @param intent 跳转意图
 * @param requestCode   返回时请求码
 * @param animType 跳转动画类型 [TransitionAnimType],默认为[TransitionAnimType.SLIDE_RIGHT_IN]
 */
@Suppress("MemberVisibilityCanBePrivate")
fun Fragment.startActivityWithAnimForResult(
    intent: Intent, requestCode: Int,
    animType: TransitionAnimType = TransitionAnimType.SLIDE_RIGHT_IN
) {
    requireActivity().apply {
        startActivityForResult(intent, requestCode)
        performTransitionAnim(animType)
    }
}

// </editor-fold desc="Fragment的Activity跳转方法">

