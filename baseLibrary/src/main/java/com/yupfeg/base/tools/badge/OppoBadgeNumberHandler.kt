package com.yupfeg.base.tools.badge

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.yupfeg.logger.ext.logw


/**
 * oppo手机的桌面红点处理类
 * * 云真机测试无效，暂无官方方案，需要向官方申请
 * @author yuPFeG
 * @date 2021/08/17
 */
class OppoBadgeNumberHandler : BadgeNumPlatformHandler {
    companion object{
        private const val PROVIDER_CONTENT_URI = "content://com.android.badge/badge"
        private const val INTENT_ACTION = "com.oppo.unsettledevent"
        private const val INTENT_EXTRA_PACKAGE_NAME = "pakeageName"
        private const val INTENT_EXTRA_BADGE_COUNT = "number"
        private const val INTENT_EXTRA_BADGE_UPGRADE_NUMBER = "upgradeNumber"
        private const val INTENT_EXTRA_BADGE_UPGRADE_COUNT = "app_badge_count"
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun setLaunchBadgeNumber(context: Context, num: Int) {
        var number = num
        if (number == 0) {
            number = -1
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                setBadgeByContentProvider(context,badgeCount = number)
            }else{
                sendBadgeCountUpdateBroadcast(context,badgeCount = number)
            }
        }catch (e : Exception){
            logw(e)
        }
    }

    /**
     * 发送桌面红点更新的系统广播
     * @param context
     * @param badgeCount 红点数量
     * */
    private fun sendBadgeCountUpdateBroadcast(context: Context,badgeCount: Int) {
        val intent = Intent(INTENT_ACTION)
        intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, context.packageName)
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount)
        intent.putExtra(INTENT_EXTRA_BADGE_UPGRADE_NUMBER, badgeCount)

        val resolveInfos = BadgeNumPlatformHandler.resolveBroadcast(context, intent)
        if (resolveInfos.isNullOrEmpty()){
            throw IllegalStateException("unable to send oppo broadcast resolve intent $intent")
        }

        for (info in resolveInfos) {
            val actualIntent = Intent(intent)
            actualIntent.setPackage(info.resolvePackageName)
            context.sendBroadcast(actualIntent)
        }
    }

    /**
     * Send request to OPPO badge content provider to set badge in OPPO home launcher.
     *
     * @param context       the context to use
     * @param badgeCount    the badge count
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setBadgeByContentProvider(context: Context, badgeCount: Int) {
        try {
            val extras = Bundle()
            extras.putInt(INTENT_EXTRA_BADGE_UPGRADE_COUNT, badgeCount)
            context.contentResolver.call(
                Uri.parse(PROVIDER_CONTENT_URI),
                "setAppBadgeCount",
                null,
                extras
            )
        } catch (ignored: Throwable) {
            throw Exception("Unable to execute Badge By Content Provider")
        }
    }

}