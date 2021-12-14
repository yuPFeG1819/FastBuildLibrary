package com.yupfeg.base.tools.system

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.yupfeg.logger.ext.logw
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * 手机设备信息相关工具类
 * @author yuPFeG
 * @date 2020/03/11
 */
@Suppress("unused")
object DevicesTools{

    /**
     * 获取手机厂商
     * @return  手机厂商
     */
    @Suppress("unused")
    @JvmStatic
    fun getDeviceBrand(): String {
        return Build.BRAND?:""
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    @Suppress("unused")
    @JvmStatic
    fun getSystemModel() : String {
        return Build.MODEL?:""
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    @Suppress("unused")
    @JvmStatic
    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE?:""
    }

    /**
     * 获取设备唯一标识符（不需要权限的标识符）
     * @param context [Context]
     * */
    @Suppress("unused")
    @JvmStatic
    fun getDeviceUUId(context: Context) : String{
        val androidId = Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        // Use the Android ID unless it's broken, in which case
        // fallback on deviceId,
        // unless it's not available, then fallback on a random
        // number which we store to a prefs file
        val uuid = if (androidId?.isNotEmpty() == true && "9774d56d682e549c" != androidId) {
            UUID.nameUUIDFromBytes(androidId.toByteArray(Charsets.UTF_8))
        }else{
            val random = Random()
            val randomId = (Integer.toHexString(random.nextInt())
                    + Integer.toHexString(random.nextInt())
                    + Integer.toHexString(random.nextInt()))
            UUID(randomId.hashCode().toLong(), getBuildInfo().hashCode().toLong())
        }
        return uuid?.toString()?:""
    }

    /**
     * 获取Build的部分信息
     *
     * @return
     */
    @JvmStatic
    fun getBuildInfo(): String {
        //这里选用了几个不会随系统更新而改变的值
        val buildSB = StringBuffer()
        buildSB.append(Build.BRAND).append("/")
        buildSB.append(Build.PRODUCT).append("/")
        buildSB.append(Build.DEVICE).append("/")
        buildSB.append(Build.ID).append("/")
        buildSB.append(Build.VERSION.INCREMENTAL)
        return buildSB.toString()
        //        return Build.FINGERPRINT;
    }

// <editor-fold desc="MAC地址">

    /**
     * 获取手机设备的MAC地址
     * @param context
     */
    @Suppress("unused")
    @SuppressLint("HardwareIds", "ObsoleteSdkInt")
    fun getMobileMAC(context : Context) : String{
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            getMacBeforeAndroid6(context)
        }else {
            //6.0以上获取MAC
            getMacFromHardware()
        }
    }

    /**Android6.0之前获取Mac地址*/
    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getMacBeforeAndroid6(context: Context) : String{
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 获取MAC地址
            val wifiInfo = wifiManager.connectionInfo
            var mac: String? = wifiInfo.macAddress
            if (null == mac) {
                // 未获取到
                mac = ""
            }
            mac
        } catch (e: Exception) {
            logw(e)
            ""
        }
    }

    /**
     * 遍历网络接口获取MAC地址，兼容至7.0以上
     */
    @SuppressLint("DefaultLocale")
    private fun getMacFromHardware() : String{
        try {
            //获取本机器所有的网络接口
            val allNetInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (netInterface in allNetInterfaces) {
                // wlan0:无线网卡 eth0：以太网卡
                if (!netInterface.name.equals("wlan0", ignoreCase = true)) continue
                //获取硬件地址，一般是MAC
                val macBytes = netInterface.hardwareAddress
                val stringBuilder = StringBuilder()
                if (macBytes != null) {
                    for (macByte in macBytes) {
                        //格式化为：两位十六进制加冒号的格式，若是不足两位，补0
                        stringBuilder.append(String.format("%02X:", macByte))
                    }
                }
                if (stringBuilder.isNotEmpty()){
                    //删除后面多余的冒号
                    stringBuilder.deleteCharAt(stringBuilder.lastIndex)
                }
                return stringBuilder.toString().lowercase(Locale.getDefault())
            }
        } catch (e: SocketException) {
            logw(e)
        }
        return "02:00:00:00:00:00"
    }

// </editor-fold>

    /**获取IMEI标识符*/
    @SuppressLint("HardwareIds")
    @Deprecated("Android10获取不到，已废弃")
    fun getIMEI(context: Context): String {
        try {
            val imei =
                //android 10设备（不管targetVersion是多少）上不能获取imei，转而使用androidId
                Settings.System.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            return if (imei.isNullOrEmpty()) "" else imei
        } catch (e: SecurityException) {
            logw(e)
        }
        return ""
    }

    /**
     * 获取IMSI标识符
     */
    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    @SuppressLint("HardwareIds", "MissingPermission")
    @Deprecated("Android10获取不到，已废弃")
    fun getIMSI(context: Context): String {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ""
        }
        var imsi = ""
        val mTelephonyMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val subscriberId = mTelephonyMgr.subscriberId?:""
            if (!TextUtils.isEmpty(subscriberId)) {
                imsi = subscriberId
            }
        }catch (e : Exception){
            logw(e)
        }

        return imsi
    }
}
