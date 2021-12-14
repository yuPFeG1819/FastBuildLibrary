package com.yupfeg.base.tools.badge

import android.content.Context
import android.os.Build

/**
 * App桌面未读消息红点管理类
 * @author yuPFeG
 * @date 2021/08/17
 */
@Suppress("unused")
object LauncherBadgeNumberManager {
    private const val HUAWEI = "Huawei"
    private const val OPPO = "oppo"
    private const val VIVO = "vivo"
    private const val XIAO_MI = "xiaomi"

    //设备只可能有一种品牌，只需要创建一次
    private val mPlatformHandler : BadgeNumPlatformHandler by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        createPlatformHandler()
    }

    /**
     * 设置桌面应用角标数量
     * @param context
     * @param num 显示的桌面角标数量
     */
    @Suppress("unused")
    @JvmStatic
    fun setBadgeNumber(context : Context, num : Int){
        mPlatformHandler.setLaunchBadgeNumber(context, num)
    }

    private fun createPlatformHandler() : BadgeNumPlatformHandler {
        return when{
            Build.MANUFACTURER.equals(HUAWEI,true)->{
                HuaweiBadgeNumHandler()
            }
            Build.MANUFACTURER.equals(XIAO_MI,true)->{
                XiaoMiBadgeNumberHandler()
            }
            Build.MANUFACTURER.equals(VIVO,true)->{
                VivoBadgeNumberHandler()
            }
            Build.MANUFACTURER.equals(OPPO,true)->{
                OppoBadgeNumberHandler()
            }
            else ->{
                DefBadgeNumPlatformHandler()
            }
        }
    }
}