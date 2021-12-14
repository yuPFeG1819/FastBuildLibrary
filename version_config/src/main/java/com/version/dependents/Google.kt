package com.version.dependents

/**
 * Google的依赖配置管理类
 * @author yuPFeG
 * @date 2020/06/11
 */
object Google {
    /**
     * 官方提供的流式布局处理方案（1.1.0以上需要Android X）
     * * [Github](https://github.com/google/flexbox-layout)
     * */
    const val flexbox = "com.google.android:flexbox:2.0.1"
    /**
     * 官方material-components组件库
     * * [Maven](https://mvnrepository.com/artifact/com.google.android.material/material)
     * * [Github](https://github.com/material-components/material-components-android/tags)
     * */
    const val material = "com.google.android.material:material:1.4.0"

    /**
     * Gson序列化库
     * [GitHub](https://github.com/google/gson)
     * */
    const val gson = "com.google.code.gson:gson:2.8.6"

    /**android studio 4.0以上。
     * 支持较旧版本的 Android 的应用程序中，包含仅在最新的Android版本中可用的标准语言API
     * 需要使用coreLibraryDesugaring进行依赖（必须）
     * [GitHub](https://github.com/google/desugar_jdk_libs)
     * */
    const val desugar_jdk_libs = "com.android.tools:desugar_jdk_libs:1.1.1"

    /**
     * Zxing二维码扫描库的基类库
     * [GitHub](https://github.com/zxing/zxing)
     */
    const val zxing_core = "com.google.zxing:core:3.4.1"
}