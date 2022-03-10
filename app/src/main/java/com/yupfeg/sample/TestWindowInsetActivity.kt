package com.yupfeg.sample

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.LiveData
import com.yupfeg.base.tools.databinding.proxy.bindingActivity
import com.yupfeg.base.tools.ext.showShortToast
import com.yupfeg.base.tools.window.*
import com.yupfeg.base.viewmodel.ext.viewModelDelegate
import com.yupfeg.base.widget.ext.DSLTextWatcher
import com.yupfeg.base.widget.ext.hideKeyboard
import com.yupfeg.base.widget.ext.showKeyboard
import com.yupfeg.logger.ext.logd
import com.yupfeg.sample.databinding.ActivityTestWindowInsetBinding
import com.yupfeg.sample.ui.TestWindowInsetViewModel
import com.yupfeg.sample.widget.ITitleConfig

/**
 * 测试WindowInsetController
 * @author yuPFeG
 * @date
 */
class TestWindowInsetActivity : AppCompatActivity(){

    private val mBinding : ActivityTestWindowInsetBinding
        by bindingActivity(R.layout.activity_test_window_inset)

    private val mViewModel : TestWindowInsetViewModel by viewModelDelegate()

    private var isInput : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.viewConfig = BindingConfig()
        mBinding.viewModel = mViewModel
        //允许沉浸式状态栏
        fitImmersiveStatusBar(isDarkText = true)

        mBinding.etTestWindowInsetInput.setWindowInsetsAnimationCompatCallBack(
            object : ViewFollowWindowInsetAnimationCallBack(
                mBinding.viewTestWindowInsetNavigationGroup
            ){
                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    val rootWindowInsetsCompat = mBinding.root.rootWindowInsetsCompat
                    val imeInsets = rootWindowInsetsCompat?.getIme()
                    logd("ime动画结束 ：top : ${imeInsets?.bottom} , bottom : ${imeInsets?.top}")
                    if (imeInsets?.bottom?:0 > 0){
                        //软键盘开启
                        mBinding.etTestWindowInsetInput.requestFocus()
                    }else{
                        //软键盘隐藏
                        mBinding.etTestWindowInsetInput.clearFocus()
                    }
                }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.viewConfig = null
    }

    inner class BindingConfig : ITitleConfig{

        override fun back() {
            finish()
        }

        override val titleName: LiveData<String>
            get() = mViewModel.titleLiveData

        val textWatcher : TextWatcher = DSLTextWatcher().apply {
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
            isInput = if (isInput){
                mBinding.etTestWindowInsetInput.hideKeyboard()
                false
            }else{
                mBinding.etTestWindowInsetInput.showKeyboard()
                true
            }

        }

    }
}