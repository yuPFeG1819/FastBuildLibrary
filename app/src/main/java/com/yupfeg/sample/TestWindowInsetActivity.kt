package com.yupfeg.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yupfeg.base.tools.showShortToast
import com.yupfeg.base.tools.window.*
import com.yupfeg.base.view.activity.bindingActivity
import com.yupfeg.base.viewmodel.ext.viewModelDelegate
import com.yupfeg.base.widget.ext.DSLTextWatcher
import com.yupfeg.base.widget.ext.hideKeyboard
import com.yupfeg.base.widget.ext.showKeyboard
import com.yupfeg.logger.ext.logd
import com.yupfeg.sample.databinding.ActivityTestWindowInsetBinding
import com.yupfeg.sample.ui.TestWindowInsetViewModel

/**
 * 测试WindowInsetController
 * @author yuPFeG
 * @date
 */
class TestWindowInsetActivity : AppCompatActivity(){

    private val mBinding : ActivityTestWindowInsetBinding
        by bindingActivity(R.layout.activity_test_window_inset)

    private val mViewModel : TestWindowInsetViewModel by viewModelDelegate()

    private var isFullScreen : Boolean = false
    private var isNavigationBar : Boolean = false
    private var isInput : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.viewConfig = BindingConfig()
        mBinding.viewModel = mViewModel
        //允许沉浸式状态栏
        fitImmersiveStatusBar(isDarkText = true)
        window.decorView.post {
            logd("当前状态栏高度${this.getStatusBarHeight()} ，" +
                    "当前导航栏高度${this.getNavigationBarHeight()} ，" +
                    " 当前软键盘高度${this.getKeyboardHeight()}")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.viewConfig = null
    }

    inner class BindingConfig{

        val textWatcher = DSLTextWatcher().apply {
            afterTextChanged = {
                logd(it?.toString()?:"")
            }
        }

        val onForceChange = View.OnFocusChangeListener { _, hasFocus ->
            logd("是否获取焦点 $hasFocus")
            isInput = hasFocus
        }

        fun naviBack(){
            finish()
        }

        fun fullScreen(){
            val pre = isFullScreen
            logd("change fullScreen mode $pre")
            isFullScreen = !pre
            //只能在Android R以上完美实现
            if (isFullScreen){
                setFullScreenMode(true)
            }else{
                setFullScreenMode(false)
            }
        }

        fun updateNavigationBar(){
            val pre = isNavigationBar
            isNavigationBar = !pre
            if(isNavigationBar) hideNavigationBar()
            else showNavigationBar()
        }

        fun bottomBtn1(){
            showShortToast("点击了bottomBtn1")
        }

        fun bottomBtn2(){
            showShortToast("点击了bottomBtn2")
        }

        fun bottomBtn3(){
            showShortToast("点击了bottomBtn3")
        }

        fun updateInputState(){
            if (isInput){
                mBinding.etTestWindowInsetInput.hideKeyboard()
                isInput = false
            }else{
                mBinding.etTestWindowInsetInput.showKeyboard()
                isInput = true
            }

        }
    }
}