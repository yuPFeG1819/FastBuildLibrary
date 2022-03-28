package com.yupfeg.base.tools.pool

import android.annotation.SuppressLint
import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import java.util.*

/**
 * WebView的视图管理类
 * @author yuPFeG
 * @date 2021/06/18
 */
object WebViewPool{
    private const val MAX_CACHE_COUNT = 2

    private val mQueue : Queue<WebView> = LinkedList()

    /**
     * 预加载webView，提前初始化webView内核
     * - 最好在IdleHandle中延迟加载
     * @param context
     * @param maxSize 预加载webView个数
     * */
    @Suppress("unused")
    @JvmStatic
    fun prepare(context: Context,maxSize : Int = 1){
        for (i in 0 until maxSize){
            val webView = createNewInstance(MutableContextWrapper(context))
            if (mQueue.size < MAX_CACHE_COUNT){
                mQueue.offer(webView)
            }
        }
    }

    /**
     * kotlin-dsl方式获取WebView实例
     * @param context context
     * @param init 可选项dsl配置
     * @return
     */
    @Suppress("unused")
    @JvmStatic
    fun getInstance(context: Context,init : WebViewOptions.()->Unit) : WebView{
        val opts = WebViewOptions().apply(init)
        return getInstance(context,opts)
    }

    /**
     * 获取WebView实例
     * @param context context
     * @param opts 配置类
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun getInstance(context: Context, opts: WebViewOptions = WebViewOptions()): WebView {
        var webView = mQueue.poll()
        webView?.apply {
            val contextWrapper = this.context as? MutableContextWrapper
            contextWrapper?.baseContext = context
            //重设webView配置
            performWebViewOptions(opts)
        }?:run {
            webView = createNewInstance(MutableContextWrapper(context), opts)
        }
        return webView!!
    }

    /**
     * 释放webView视图
     * - 内部执行通用的webView销毁操作
     * - destroy后无法复用，再次使用会出现白屏而且后续webView创建速度很快，基本不需要复用
     * @param instance
     * */
    @JvmStatic
    fun releaseView(instance: WebView){
        instance.apply {
            stopLoading()
            val contextWrapper = (this.context as? MutableContextWrapper)
            contextWrapper?.baseContext = contextWrapper?.applicationContext
            //加载空白页，移除正在进行的请求
            loadDataWithBaseURL(
                null, "", "text/html", "utf-8", null
            )
            webChromeClient = null
            clearHistory()
            clearCache(true)
            //从父布局中移除视图
            (parent as? ViewGroup)?.removeView(instance)
            //销毁webView内所有对象
            removeAllViews()
            destroy()
            //重新添加到缓存队列
//            mQueue.offer(this)
        }
    }

    /**
     * 销毁内部缓存
     * */
    @Suppress("unused")
    fun destroy(){
        while (mQueue.isNotEmpty()){
            mQueue.poll()?.apply {
                //销毁webView内所有对象
                removeAllViews()
                destroy()
            }
        }
    }

    /**
     * 创建新的webView实例
     * @param context
     * @param opts 可选项
     * */
    @JvmStatic fun createNewInstance(
        context: Context, opts : WebViewOptions = WebViewOptions()
    ) : WebView {
        return WebView(context).performWebViewOptions(opts)
    }

    /**
     * 配置webView的可选设置
     * @param
     * */
    private fun WebView.performWebViewOptions(opts: WebViewOptions) : WebView{
        settings.initWebViewSetting(opts)
        //在原有user Agent后面拼接固定字段
        opts.extraUserAgent?.also {
            settings.userAgentString = "${settings.userAgentString} $it"
        }
        //设置滚动条的样式
        scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        opts.webChromeClient?.also {
            //重写WebChromeClient监听网页加载的进度等操作
            webChromeClient = it
        }
        opts.webViewClient?.also {
            //监听网页的跳转和资源加载
            webViewClient = it
        }
        //避免白屏
        this.setBackgroundColor(Color.TRANSPARENT)
        return this
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    @JvmStatic private fun WebSettings.initWebViewSetting(opts : WebViewOptions){
        if (opts.isSupportZoom){
            //支持缩放功能
            setSupportZoom(true)
            builtInZoomControls = true
            //不显示原生缩放控件
            displayZoomControls = false
        }else{
            //禁用webView缩放
            builtInZoomControls = false
        }
        //允许WebView使用File协议
        allowFileAccess = true
        //设置网页自适应屏幕大小与useWideViewPort配合
        loadWithOverviewMode = opts.isLoadWithOverviewMode
        //设置为使用WebView推荐的窗口
        useWideViewPort = opts.isLoadWithOverviewMode

        defaultTextEncodingName = opts.textEncodingName
        //在页面加载完才加载图片
        loadsImagesAutomatically = opts.isLoadsImagesAutomatically
        //大部分网页需要自己保存一些数据,这个时候就的设置下面这个属性
        //开启 DOM storage API 功能 较大存储空间，使用简单
        domStorageEnabled = opts.isDomStorageEnabled
        //设置数据库缓存路径 存储管理复杂数据 方便对数据进行增加、删除、修改、查询 不推荐使用
        databaseEnabled = opts.isDatabaseEnabled
        //设置缓存模式,使用默认缓存
        cacheMode = opts.cacheMode
        //是否支持多窗口，配合window.open时，需要禁用，否则不会触发重定向url方法
        setSupportMultipleWindows(opts.isMultipleWindows)
        //设置WebView是否可以由JavaScript自动打开窗口，默认为false，通常与JavaScript的window.open()配合使用
        javaScriptCanOpenWindowsAutomatically = opts.isJavaScriptCanOpenWindowsAutomatically

        //Android 5.0以上，需要允许加载http与https混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mixedContentMode = opts.mixedContentMode
        }
        //说分批允许开启JS资源
        //版本大于17,需要在addJavascriptInterface函数添加的对象内部加上注解 @JavascriptInterface
        javaScriptEnabled = opts.isJavaScriptEnabled
    }

}