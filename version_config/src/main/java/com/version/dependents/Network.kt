package com.version.dependents

/**
 * 网络相关依赖库
 * *[Retrofit的GitHub](https://github.com/square/retrofit)
 * @author yuPFeG
 * @date 2021/02/04
 */
@Suppress("MemberVisibilityCanBePrivate")
object Network {
    const val retrofitVersion = "2.9.0"
    const val okhttpVersion = "4.9.0"

    const val okHttp3 = "com.squareup.okhttp3:okhttp:$okhttpVersion"
    @Deprecated("暂无使用，在okHttp内部已集成2.8.0版本")
    const val okIo = "com.squareup.okio:okio:2.8.0"
    /**
     * fastJson.android包版本
     * * [fastJson](https://github.com/alibaba/fastjson)
     * */
    @Deprecated("暂无使用fastJson")
    const val fastJsonAndroid = "com.alibaba:fastjson:1.1.72.android"

    const val retrofit2 = "com.squareup.retrofit2:retrofit:$retrofitVersion"
    /**retrofit转化成RxJava3事件流*/
    const val retrofitRxJava3Adapter = "com.squareup.retrofit2:adapter-rxjava3:$retrofitVersion"
    /**retrofit支持Gson解析*/
    const val retrofitGson2 = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
    /**retrofit支持解析为标准字符串*/
    @Deprecated("暂无使用")
    const val retrofitScalars2 = "com.squareup.retrofit2:converter-scalars:$retrofitVersion"
    /**Retrofit的fastJson解析器（暂无使用）*/
    @Deprecated("暂无使用fastJson")
    const val retrofitFastJson2 = "org.ligboy.retrofit2:converter-fastjson-android:2.2.0"
}