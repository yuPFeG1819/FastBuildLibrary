package com.version.dependents

/**
 * RxJava相关依赖库
 * @author yuPFeG
 * * [RxJava](https://github.com/ReactiveX/RxJava)
 * * [RxAndroid](https://github.com/ReactiveX/RxAndroid)
 * * [RxKotlin](https://github.com/ReactiveX/RxKotlin)
 * * [RxRelay](https://github.com/JakeWharton/RxRelay)
 * */
object RxJava{
    const val rxJava3Version = "3.0.10"
    const val rxJava3WrapperVersion = "3.0.0"
    const val rxKotlin3Version = "3.0.1"

    @Deprecated("已废弃")
    const val rxJava2 = "io.reactivex.rxjava2:rxjava:2.2.19"
    @Deprecated("已废弃")
    const val rxAndroid2 = "io.reactivex.rxjava2:rxandroid:2.1.1"
    @Deprecated("已废弃")
    const val rxKotlin2 = "io.reactivex.rxjava2:rxkotlin:2.4.0"
    @Deprecated("已废弃")
    const val rxRelay2 = "com.jakewharton.rxrelay2:rxrelay:2.1.1"

    const val rxJava3GroupName = "io.reactivex.rxjava3:rxjava"

    const val rxJava3 = "io.reactivex.rxjava3:rxjava:$rxJava3Version"
    const val rxAndroid3 = "io.reactivex.rxjava3:rxandroid:$rxJava3WrapperVersion"
    const val rxKotlin3 = "io.reactivex.rxjava3:rxkotlin:$rxKotlin3Version"
    const val rxRelay3 = "com.jakewharton.rxrelay3:rxrelay:$rxJava3WrapperVersion"

    /**
     * 基于RxJava的权限请求库
     * * [GitHub](https://github.com/tbruyelle/RxPermissions)
     * */
    object RxPermission{
        private const val rxPermissionVersion = "0.12"

        /**基于RxJava2的权限请求库*/
        @Deprecated("仅支持RxJava2，已废弃")
        const val rxJava2 = "com.github.tbruyelle:rxpermissions:0.10.2"
        /**
         * 基于RxJava3的权限请求库
         * */
        const val rxJava3 = "com.github.tbruyelle:rxpermissions:$rxPermissionVersion"
    }

    /**
     * 自动管理RxJava订阅生命周期
     * [GitHub](https://github.com/uber/AutoDispose)
     * */
    object AutoDispose{
        @Deprecated("仅适配RxJava2，已废弃")
        private const val autoDisposeVersion = "1.4.0"

        private const val autoDispose2Version = "2.1.1"

        /**自动管理rxJava2订阅生命周期*/
        @Deprecated("仅适配RxJava2，已废弃")
        const val rxJava2 = "com.uber.autodispose:autodispose:$autoDisposeVersion"
        @Deprecated("仅适配RxJava2，已废弃")
        const val RxJava2ArchComponents =
            "com.uber.autodispose:autodispose-android-archcomponents:$autoDisposeVersion"
        /**自动管理rxJava3订阅生命周期*/
        const val rxJava3 = "com.uber.autodispose2:autodispose:$autoDispose2Version"
        const val rxJava3Lifecycle =
            "com.uber.autodispose2:autodispose-androidx-lifecycle:$autoDispose2Version"
    }

    /**
     * 基于RxJava的View绑定，拓展函数，将View事件，转化为RxJava事件流
     * [GitHub](https://github.com/JakeWharton/RxBinding)
     * */
    @Deprecated("已废弃")
    object RxBinding{
        private const val rxBinding4Version = "4.0.0"

        /**基于RxJava2的View绑定，拓展函数，将View事件，转化为RxJava2事件流*/
        @Deprecated("已废弃")
        const val core3 = "com.jakewharton.rxbinding3:rxbinding-core:3.1.0"
        @Deprecated("已废弃")
        const val appcompat3 = "com.jakewharton.rxbinding3:rxbinding-appcompat:3.1.0"
        @Deprecated("已废弃")
        const val recyclerview3 = "com.jakewharton.rxbinding3:rxbinding-recyclerview:3.1.0"
        @Deprecated("已废弃")
        const val swipeRefreshLayout3 = "com.jakewharton.rxbinding3:rxbinding-swiperefreshlayout:3.1.0"
        @Deprecated("已废弃")
        const val material3 = "com.jakewharton.rxbinding3:rxbinding-rxbinding-material:3.1.0"

        /**
         * 基于RxJava3的View绑定，拓展函数，将View事件，转化为RxJava3事件流
         * */
        const val core4 =
            "com.jakewharton.rxbinding4:rxbinding-core:$rxBinding4Version"
        const val appcompat4 =
            "com.jakewharton.rxbinding4:rxbinding-appcompat:$rxBinding4Version"
        const val recyclerview4 =
            "com.jakewharton.rxbinding4:rxbinding-recyclerview:$rxBinding4Version"
        const val swipeRefreshLayout4 =
            "com.jakewharton.rxbinding4:rxbinding-swiperefreshlayout:$rxBinding4Version"

        const val material4 = "com.jakewharton.rxbinding4:rxbinding-material:$rxBinding4Version"
    }
}