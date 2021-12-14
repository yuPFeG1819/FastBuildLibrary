package com.version.dependents

/**
 * 第三方依赖配置库
 * @author yuPFeG
 * @date 2020/06/11
 */
object ThirdParty {

    /**
     * 智能（高拓展）下拉刷新框架
     * * [GitHub](https://github.com/scwang90/SmartRefreshLayout)
     * */
    object SmartRefreshLayout{
        //核心库
        const val base = "com.scwang.smart:refresh-layout-kernel:2.0.1"
    }

    /**
     * Kotlin语言支持库
     * [Github](https://github.com/JetBrains/kotlin)
     * */
    object Kotlin{
        /**
         * kotlin插件与api版本号
         * 需要与dependencies.gradle内的versions.kotlin_plugin保持一致
         * */
        private const val kotlinVersion = "1.5.31"
        private const val coroutinesVersion = "1.5.2"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"

        /**
         * kotlin 协程基础库
         * */
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"

        /**
         * kotlin协程Android拓展库
         * * 已包含基础库
         * [GitHub](https://github.com/Kotlin/kotlinx.coroutines)
         * */
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"

        /**
         * kotlin协程测试库
         * */
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"

        /**kotlin反射库，大约2m左右*/
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    }

    /**
     * Glide图片加载库
     * * [GitHub](https://github.com/bumptech/glide)
     * */
    object Glide{
        private const val glideVersion = "4.12.0"

        const val glide = "com.github.bumptech.glide:glide:$glideVersion"
        const val compiler = "com.github.bumptech.glide:compiler:$glideVersion"
        const val okHttp = "com.github.bumptech.glide:okhttp3-integration:$glideVersion"
        const val glideAnnotations = "com.github.bumptech.glide:annotations:$glideVersion"
    }

    /**
     * 适配了Android 11的toast框架
     * * [GitHub](https://github.com/getActivity/ToastUtils)
     * */
    const val toastUtils = "com.github.getActivity:ToastUtils:9.2"

    /**
     * PermissionX
     * * 整合了特殊权限请求，并适配了Android 12
     * * [GitHub](https://github.com/guolindev/PermissionX)
     */
    @Deprecated("使用ResultAPI的封装更方便")
    const val permissionX = "com.guolindev.permissionx:permissionx:1.6.0"

    /**
     * lottie动画库
     * * [Github](https://github.com/airbnb/lottie-android)
     * */
    const val lottie = "com.airbnb.android:lottie:3.4.0"

    /**
     * 自用日志输出库
     * * [Gitee](https://gitee.com/yupfeg/logger)
     */
    const val logger = "com.gitee.yupfeg:logger:1.0.4"
    /**
     * 自用网络库
     * * [Gitee](https://gitee.com/yupfeg/http_request_mediator)
     */
    const val httpMediator = "com.gitee.yupfeg:http_request_mediator:1.0.5"

    /**
     * 自用的单次执行LiveData封装
     * */
    const val stateLiveData = "com.gitee.yupfeg:state-live-data-wrapper:1.0.1"

    /**
     * 自用ResultAPI封装库
     * * [Gitee](https://gitee.com/yupfeg/easy-result-api)
     * */
    const val easyResultApi = "com.gitee.yupfeg:easy-result-api:1.1.0"

    /**
     * Mmkv 基于mmap内存映射的key-value储存框架
     * * [GitHub](https://github.com/tencent/mmkv)
     */
    const val mmkv = "com.tencent:mmkv-static:1.2.10"

    /**
     * 日志记录库
     * * [GitHub](https://github.com/JakeWharton/timber)
     * */
    const val timberLog = "com.jakewharton.timber:timber:4.7.1"
    /**
     * 仿iOS的PickerView控件
     * * [GitHub](https://github.com/Bigkoo/Android-PickerView)
     */
    const val androidPickerView = "com.contrarywind:Android-PickerView:4.1.9"

    /**
     * loading库.
     * * [GitHub](https://github.com/ybq/Android-SpinKit)
     */
    const val spinKitLoading = "com.github.ybq:Android-SpinKit:1.4.0"

    /**
     * 第三方loading动画组件版本，已停止维护.
     * * [GitHub](https://github.com/81813780/AVLoadingIndicatorView)
     */
    @Deprecated("可用，但已停止维护")
    const val avLoadingIndicator = "com.wang.avi:library:2.1.3"

    /**阿里推送*/
    object AliPush{
        const val push_aar = "com.aliyun.ams:alicloud-android-push:3.1.9.1@aar"
        const val utils = "com.aliyun.ams:alicloud-android-utils:1.1.6.4"
        const val beacon = "com.aliyun.ams:alicloud-android-beacon:1.0.3"
        const val ut = "com.aliyun.ams:alicloud-android-ut:5.4.3"
        const val utdid = "com.aliyun.ams:alicloud-android-utdid:1.5.2"
    }

    /**
     * 中文与拼音转换库
     * * [GitHub](https://github.com/promeG/TinyPinyin)
     * */
    object TinPinYin{
        /**TinyPinyin核心包，约80KB*/
        const val base = "com.github.promeg:tinypinyin:2.0.3"
        /**可选，适用于Android的中国地区词典*/
        const val android = "com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3"
    }

    object ZXing{
        /**
         * 基础zxing库
         * */
        const val base = "com.google.zxing:core:3.3.3"

        /**
         * ZXing的精简版，优化扫码和生成二维码/条形码，
         * 内部已集成 zxing库，基于CameraX
         * * [GitHub](https://github.com/jenly1314/ZXingLite)
         * */
        const val zxingLite = "com.github.jenly1314:zxing-lite:2.1.0"

        /**
         * zxing二维码扫码库 ,4.0+需要androidX
         *
         * * [GitHub](https://github.com/journeyapps/zxing-android-embedded)
         */
        const val androidEmbedded = "com.journeyapps:zxing-android-embedded:4.1.0"
    }

    /**
     * 友盟相关
     * */
    object UMeng{
        const val common = "com.umeng.umsdk:common:9.4.0"
        const val asms = "com.umeng.umsdk:asms:1.2.3"
        /**错误收集统计,错误分析升级为独立SDK，看crash数据请一定集成*/
        const val apm = "com.umeng.umsdk:apm:1.4.0"
    }

}