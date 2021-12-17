package com.yupfeg.sample.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Messenger
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.yupfeg.base.tools.pool.WebViewPool
import com.yupfeg.base.view.activity.BaseActivity
import com.yupfeg.base.view.activity.bindingActivity
import com.yupfeg.base.viewmodel.ext.viewModelDelegate
import com.yupfeg.sample.R
import com.yupfeg.sample.TestMessengerService
import com.yupfeg.sample.TestServiceAIDL
import com.yupfeg.sample.databinding.ActivityTestResultApiBinding

/**
 *
 * @author yuPFeG
 * @date
 */
class TestResultApiActivity : BaseActivity(){
    private val mViewModel : TestResultViewModel by viewModelDelegate()
    private val mBinding : ActivityTestResultApiBinding by bindingActivity(layoutId)
    private var mMessenger : Messenger? = null
    private var mTestAIDL : TestServiceAIDL? = null

    private var mWebView : WebView ?= null

    private val mConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mTestAIDL = TestServiceAIDL.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    override fun onResume() {
        super.onResume()
        mWebView?.apply {
            onResume()
            resumeTimers()
        }
    }

    override fun onPause() {
        mWebView?.apply {
            onPause()
            pauseTimers()
        }
        super.onPause()
    }

    override fun onDestroy() {
        mWebView?.also{
            WebViewPool.releaseView(it)
            mWebView = null
        }
        super.onDestroy()
    }

    /**布局文件id*/
    override val layoutId: Int
        get() = R.layout.activity_test_result_api

    /**
     * 初始化控件
     * * tip : 如果使用原始的布局方式，需要注意设置[setContentView]，
     * 否则推荐使用DataBinding，利用by关键字，通过`bindingActivity`函数代理DataBinding类，
     * 或者利用`DataBindingUtil.setContentView<T>(this, layoutId)`
     * */
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.config = BindingConfig()
        mViewModel.bindUseCaseLifecycle(this.lifecycle)
        val intent = Intent(this, TestMessengerService::class.java)
        bindService(intent,mConnection, BIND_AUTO_CREATE)

        mWebView = WebViewPool.acquireInstance(this)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mBinding.fragmentWebview.addView(mWebView,layoutParams)
        mWebView?.loadUrl("https://juejin.cn/")
    }

    /**初始化数据*/
    override fun initData() {}

    inner class BindingConfig{

        fun normalBack(){
            finish()
        }

        fun resultDataBack(){
            val intent = Intent().apply {
                putExtras(bundleOf("key" to "test1"))
            }
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}