package com.yupfeg.base.tools.pool

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * WebView的视图管理类
 * @author yuPFeG
 * @date 2021/06/18
 */
object WebViewManager {

    //TODO 后续添加一个初始化操作，在应用启动时，提前初始化WebView内核

    @JvmStatic fun createSampleWebView(context: Context) : WebView{
        val webView = WebView(context.applicationContext)
        //禁用js
        webView.settings.javaScriptEnabled = false
        return webView
    }

    /**
     * 获取已有的webView的实例
     * */
    @JvmStatic fun getWebViewInstance(
        context: Context,
        mWebChromeClient : WebChromeClient? = null,
        mWebViewClient : WebViewClient? = null
    ) : WebView{
        //TODO 后续可以考虑使用webView对象池，公用webView实例
        return createNewWebViewInstance(context, mWebChromeClient, mWebViewClient)
    }


    /**
     * 创建新的webView实例
     * */
    @JvmStatic fun createNewWebViewInstance(
        context: Context,
        mWebChromeClient : WebChromeClient? = null,
        mWebViewClient : WebViewClient? = null
    ) : WebView {
        //TODO 后续可以尝试dsl方式配置web
        return WebView(context.applicationContext).apply {
            settings.initWebViewSetting()
            //在原有user Agent后面拼接固定字段，标识为来自app访问
//            settings.userAgentString =
//                "${settings.userAgentString} ${AppConstant.WEB_USER_AGENT_APP_TAG}"
            //设置滚动条的样式
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            mWebChromeClient?.also {
                //重写WebChromeClient监听网页加载的进度等操作
                webChromeClient = it
            }
            mWebViewClient?.also {
                //监听网页的跳转和资源加载
                webViewClient = it
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic private fun WebSettings.initWebViewSetting(){
        setSupportZoom(true)
        //设置网页自适应屏幕大小与useWideViewPort配合
        loadWithOverviewMode = true
        //设置为使用WebView推荐的窗口
        useWideViewPort = true
        //禁用webView缩放
        builtInZoomControls = false
        defaultTextEncodingName = "utf-8"
        loadsImagesAutomatically = true
        //大部分网页需要自己保存一些数据,这个时候就的设置下面这个属性
        //开启 DOM storage API 功能 较大存储空间，使用简单
        domStorageEnabled = true
        //设置数据库缓存路径 存储管理复杂数据 方便对数据进行增加、删除、修改、查询 不推荐使用
        databaseEnabled = false
        //设置缓存模式,使用默认缓存
        cacheMode = WebSettings.LOAD_DEFAULT
        //禁用多窗口，否则window.open不会触发重定向url方法
        setSupportMultipleWindows(false)
        //设置WebView是否可以由JavaScript自动打开窗口，默认为false，通常与JavaScript的window.open()配合使用
        javaScriptCanOpenWindowsAutomatically = true
        //允许开启JS资源,安卓版本大于17,加上注解 @JavascriptInterface
        javaScriptEnabled = true

    }
}