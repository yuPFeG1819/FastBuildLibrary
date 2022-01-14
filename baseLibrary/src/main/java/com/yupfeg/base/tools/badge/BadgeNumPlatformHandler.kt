package com.yupfeg.base.tools.badge

import android.content.Context


/**
 * 手机品牌平台的桌面红点处理器
 * @author yuPFeG
 * @date 2021/08/17
 */
interface BadgeNumPlatformHandler {
    /**
     * 修改桌面红点标记
     * @param context
     * @param num 未读消息数量
     * */
    fun setLaunchBadgeNumber(context: Context, num : Int)

}

class DefBadgeNumPlatformHandler : BadgeNumPlatformHandler {
    override fun setLaunchBadgeNumber(context: Context, num: Int) {
        // do noting
    }

}