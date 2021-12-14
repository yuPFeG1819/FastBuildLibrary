package com.yupfeg.base.tools.badge

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import com.yupfeg.logger.ext.logw
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * 小米平台的桌面红点处理类
 * * 小米的桌面红点是与通知绑定的，只要应用在前台就会清除桌面角标
 * * [小米官方适配文档](https://dev.mi.com/console/doc/detail?pId=2321)
 * @author yuPFeG
 * @date 2021/08/17
 */
class XiaoMiBadgeNumberHandler : BadgeNumPlatformHandler {
    companion object{
        /**
         * 仅用于设置小米的红点
         * * 实际不会去创建该通知渠道
         * */
        const val CHANNEL_ID = "${Int.MIN_VALUE}"
    }

    override fun setLaunchBadgeNumber(context: Context, num: Int) {
        val mNotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(context)
        //只适用于后台进程，只要应用处于前台，所有角标都会被清除，直到新的推送消息到达
        try {
            val field: Field = notification.javaClass.getDeclaredField("extraNotification")
            val extraNotification = field.get(notification)
            val method: Method = extraNotification.javaClass.getDeclaredMethod(
                "setMessageCount",
                Int::class.javaPrimitiveType
            )
            method.invoke(extraNotification, num)
        } catch (e: Exception) {
            logw(e)
        }
        mNotificationManager.notify(0, notification)
    }

    private fun createNotification(context: Context) : Notification{
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = context.packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("")
            .setContentText("")
            .setSmallIcon(resolveInfo?.iconResource?:0)
            .build()
    }
}