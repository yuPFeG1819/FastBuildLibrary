package com.yupfeg.base.tools.badge

import android.content.Context
import android.net.Uri
import androidx.core.os.bundleOf

/**
 * 华为的桌面红点数目处理类
 * * 2021/08/17测试可用
 * * 需要在manifest.xml内添加<uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE" />
 * @author yuPFeG
 * @date 2021/08/17
 */
class HuaweiBadgeNumHandler : BadgeNumPlatformHandler {
    override fun setLaunchBadgeNumber(context: Context, num: Int) {
        try {
            var number = num
            if (num < 0) number = 0
            val launchClassName = context.packageManager
                .getLaunchIntentForPackage(context.packageName)?.component?.className
            val bundle = bundleOf(
                "package" to context.packageName,
                "class" to launchClassName,
                "badgenumber" to number
            )
            context.contentResolver.call(
                Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                "change_badge", null, bundle
            )
        } catch (e : Exception) {
//            logw(e)
        }
    }
}