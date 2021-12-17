package com.yupfeg.base.tools.pool

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient

/**
 * 网络视图的
 * @author yuPFeG
 * @date 2021/12/15
 */
class WebViewOptions {
    /**是否支持缩放*/
    @JvmField
    var isSupportZoom = true

    /**网页是否自适应屏幕大小*/
    @JvmField
    var isLoadWithOverviewMode = true

    /**默认编码方式*/
    @JvmField
    val textEncodingName = "utf-8"

    /**是否加载网页后才加载图片*/
    @JvmField
    var isLoadsImagesAutomatically = true

    /**
     * 大部分网页需要自己保存一些数据,这个时候就的设置下面这个属性，
     * 开启 DOM storage API 功能 较大存储空间，使用简单
     */
    @JvmField
    var isDomStorageEnabled = true

    /**设置数据库缓存路径 存储管理复杂数据 方便对数据进行增加、删除、修改、查询 不推荐使用*/
    @JvmField
    var isDatabaseEnabled = false

    /**
     * 设置缓存模式,使用默认缓存
     * - LOAD_CACHE_ONLY：只使用本地缓存，不进行网络请求
     * - LOAD_NO_CACHE：不使用本地缓存，只通过网络请求
     * - LOAD_CACHE_ELSE_NETWORK：只要本地有缓存就进行使用，否则就通过网络请求
     * - LOAD_DEFAULT：根据 Http 协议来决定是否进行网络请求
     * */
    @JvmField
    var cacheMode = WebSettings.LOAD_DEFAULT

    /**
     * 是否支持多窗口，默认不支持，否则window.open不会触发重定向url方法
     * */
    @JvmField
    var isMultipleWindows = false

    /**
     * 设置WebView是否可以由JavaScript自动打开窗口，默认为false，
     * - 通常与JavaScript的window.open()配合使用
     * */
    @JvmField
    var isJavaScriptCanOpenWindowsAutomatically = true

    /**
     * http与https混合内容模式，默认允许
     * - 在Android 5.0以上，允许加载http与https混合内容
     * */
    @JvmField
    var mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

    /**
     * 初始是否支持加载JS
     * */
    @JvmField
    var isJavaScriptEnabled = true

    /**
     * 额外的user-agent
     * * 仅提供添加于原始userAgentString后面
     * */
    @JvmField
    var extraUserAgent : String? = null

    /**
     * webView的加载回调
     * */
    @JvmField
    var webChromeClient : WebChromeClient? = null

    /**webView的请求回调*/
    @JvmField
    var webViewClient : WebViewClient? = null
}