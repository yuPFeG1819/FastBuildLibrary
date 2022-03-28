package com.yupfeg.sample.ui

import android.content.Intent
import android.os.Bundle
import android.os.Messenger
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.yupfeg.base.tools.databinding.proxy.bindingActivity
import com.yupfeg.base.tools.pool.WebViewPool
import com.yupfeg.base.tools.ext.showShortToast
import com.yupfeg.base.view.activity.BaseActivity
import com.yupfeg.base.viewmodel.ext.viewModelDelegate
import com.yupfeg.sample.R
import com.yupfeg.sample.databinding.ActivityTestResultApiBinding
import com.yupfeg.sample.ui.list.TestListActivity

/**
 *
 * @author yuPFeG
 * @date
 */
class TestResultApiActivity : BaseActivity(){
    private val mBinding : ActivityTestResultApiBinding by bindingActivity(layoutId)
    private val mViewModel : TestResultViewModel by viewModelDelegate()

    private var mMessenger : Messenger? = null
//    private var mTestAIDL : TestServiceAIDL? = null

    private var mWebView : WebView ?= null

//    private val mConnection = object : ServiceConnection{
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            mTestAIDL = TestServiceAIDL.Stub.asInterface(service)
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//
//        }
//
//    }

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

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.config = BindingConfig()
        mBinding.viewModel = mViewModel
//        val intent = Intent(this, TestMessengerService::class.java)
//        bindService(intent,mConnection, BIND_AUTO_CREATE)

        mWebView = WebViewPool.getInstance(this)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mBinding.fragmentWebview.addView(mWebView,layoutParams)
        mWebView?.loadUrl("https://juejin.cn/")
    }

    /**初始化数据*/
    override fun initData() {
        mViewModel.spannableClickEvent.observe(this){
            showShortToast("点击SpannableString")
            startActivity(Intent(this@TestResultApiActivity,TestListActivity::class.java))
        }
    }

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