package com.yupfeg.base.tools.badge

import android.content.Context
import android.content.Intent


/**
 * vivo手机的桌面红点处理类
 * * 需要在Manifest.xml内添加 <uses-permission android:name="com.vivo.notification.permission.BADGE_ICON" />
 * * 还需要手动到'系统设置'->'通知与状态栏'->’应用通知管理‘->app->'桌面角标'开启，默认是关闭状态
 * * [vivo官方适配文档](https://dev.vivo.com.cn/documentCenter/doc/459)
 * @author yuPFeG
 * @date 2021/08/17
 */
class VivoBadgeNumberHandler : BadgeNumPlatformHandler {

    override fun setLaunchBadgeNumber(context: Context, num: Int) {
        val intent = Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM")
        val launchClassName = context.packageManager
            .getLaunchIntentForPackage(context.packageName)?.component?.className
        intent.putExtra("packageName", context.packageName)
        intent.putExtra("className", launchClassName)
        intent.putExtra("notificationNum", num)
        context.sendBroadcast(intent)
    }
}