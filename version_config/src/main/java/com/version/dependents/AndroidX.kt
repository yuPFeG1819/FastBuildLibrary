package com.version.dependents

/**
 * 官方依赖配置类
 *
 * 不带 ktx 后缀的为 java 依赖，核心功能在此库
 * 带 ktx 后缀为 kotlin 依赖，提供很多方便的扩展函数, ktx 默认引入不带 ktx 的库
 * [google androidX的依赖地址版本](https://developer.android.google.cn/jetpack/androidx/versions)
 * @author yuPFeG
 * @date 2020/06/11
 */
object AndroidX{
    /**
     * Appcompat
     * appcompat1.3.1已包含了 Activity 1.2.4 和 Fragment 1.3.6
     */
    const val appcompat = "androidx.appcompat:appcompat:1.3.1"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"
    const val coreKtx = "androidx.core:core-ktx:1.7.0"
    /**
     * Activity库，
     * [Activity版本更新](https://developer.android.google.cn/jetpack/androidx/releases/activity#version_130_3)
     * */
    const val activityKtx = "androidx.activity:activity-ktx:1.3.1"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.0"
    const val cardView = "androidx.cardview:cardview:1.0.0"
    const val multiDex = "androidx.multidex:multidex:2.0.1"
    @Deprecated("已用viewpager2替代")
    const val viewpager = "androidx.viewpager:viewpager:1.0.0"
    const val viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"
    /**
     * [异步布局IO文件导入方案](https://developer.android.google.cn/jetpack/androidx/releases/asynclayoutinflater)
     * */
    const val asyncLayoutInflater = "androidx.asynclayoutinflater:asynclayoutinflater:1.0.0"

    /***
     * Fragment库，
     * * [Jetpack 主要组件的依赖及传递关系](https://juejin.im/post/5e567ee1518825494466a938)
     * * [Fragment版本更新](https://developer.android.google.cn/jetpack/androidx/releases/fragment#1.3.6)
     */
    object Fragment {
        private const val fragmentVersion = "1.3.6"
        const val fragment = "androidx.fragment:fragment:$fragmentVersion"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:$fragmentVersion"
        const val fragmentTesting = "androidx.fragment:fragment-testing:$fragmentVersion"
    }
    /**
     * 带 ktx 后缀的库与不带 ktx 后缀的区别见
     * * [Jetpack 主要组件的依赖及传递关系](https://juejin.im/post/5e567ee1518825494466a938)
     */
    object Lifecycle {
        private const val lifecycle_version = "2.4.0"
        const val runtime = "androidx.lifecycle:lifecycle-runtime:$lifecycle_version"
        const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"

        const val liveData = "androidx.lifecycle:lifecycle-livedata:$lifecycle_version"
        const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
        /**
         * kotlin下使用kapt依赖，已废弃
         *
         * First replace
         * annotationProcessor "androidx.lifecycle:lifecycle-compiler:*version*"
         * kapt "androidx.lifecycle:lifecycle-compiler:*version*"
         * with
         * implementation "androidx.lifecycle:lifecycle-common-java8:*version*"
         * Then remove any OnLifecycleEvent annotations from Observer classes and make them
         * implement the DefaultLifecycleObserver interface.  Issue id: LifecycleAnnotationProcessorWithJava8
         * More info: [https://d.android.com/r/studio-ui/lifecycle-release-notes]
         * */
        @Deprecated("依赖lifecycle-common-java8能获得更好的编译速度")
        const val compiler = "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"

//        const val common = "androidx.lifecycle:lifecycle-common:$lifecycle_version"
//        @Deprecated("2.4.0以后统一移动到comm库了，没有java8这个单独的依赖库")
        const val commonJava8 = "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
        const val viewModelSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"
        const val service = "androidx.lifecycle:lifecycle-service:$lifecycle_version"
        /**监听APP应用的生命周期库*/
        const val process = "androidx.lifecycle:lifecycle-process:$lifecycle_version"

    }

    object Navigation {
        private const val nav_version = "2.3.5"

        // 无需直接引用
        const val runtime = "androidx.navigation:navigation-runtime:$nav_version"

        const val fragment = "androidx.navigation:navigation-fragment:$nav_version"
        const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$nav_version"

        const val ui = "androidx.navigation:navigation-ui:$nav_version"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:$nav_version"

        // Dynamic Feature Module Support
        const val dynamic = "androidx.navigation:navigation-dynamic-features-fragment:$nav_version"

        // Testing Navigation
        const val testing = "androidx.navigation:navigation-testing:$nav_version"
    }

    /**
     * [Room官方文档](https://developer.android.google.cn/jetpack/androidx/releases/room#version_230_3)
     * * 在2.3.0版本后，已支持RxJava3
     * */
    object Room {
        private const val room_version = "2.3.0"
        const val runtime = "androidx.room:room-runtime:$room_version"
        // for java use annotationProcessor , for kotlin use kapt
        const val compiler = "androidx.room:room-compiler:$room_version"
        // optional - Kotlin Extensions and Coroutines support for Room
        const val roomKtx = "androidx.room:room-ktx:$room_version"
        // optional - RxJava support for Room
        const val rxjava2 = "androidx.room:room-rxjava2:$room_version"
        // optional - RxJava3 support for Room
        const val rxjava3 = "androidx.room:room-rxjava3:$room_version"
        // optional - Guava support for Room, including Optional and ListenableFuture
        const val guava = "androidx.room:room-guava:$room_version"
        // Test helpers
        const val testing = "androidx.room:room-testing:$room_version"
    }

    /**
     * [Hilt版本更新](https://developer.android.google.cn/jetpack/androidx/releases/hilt#version_100_3)
     * [Hilt官方集成文档](https://developer.android.com/training/dependency-injection/hilt-jetpack)
     * */
    object Hilt{

    }
}