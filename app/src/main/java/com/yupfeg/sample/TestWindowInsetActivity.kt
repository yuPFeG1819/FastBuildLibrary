package com.yupfeg.sample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.yupfeg.base.tools.system.*
import com.yupfeg.base.view.activity.bindingActivity
import com.yupfeg.base.widget.ext.fitSystemNavigationWindow
import com.yupfeg.base.widget.ext.fitSystemStatusBar
import com.yupfeg.logger.ext.logd
import com.yupfeg.sample.databinding.ActivityTestWindowInsetBinding

/**
 * 测试WindowInsetController
 * @author yuPFeG
 * @date
 */
class TestWindowInsetActivity : AppCompatActivity(){

    private val mBinding : ActivityTestWindowInsetBinding
        by bindingActivity(R.layout.activity_test_window_inset)

    private var isFullScreen : Boolean = false
    private var isNavigationBar : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.viewConfig = BindingConfig()
        immersiveStatusBar(isDarkText = true)
        mBinding.viewTestWindowInsetTitleGroup.fitSystemStatusBar{
            logd("full screen state = $it")
            isFullScreen = it
        }
        mBinding.viewTestWindowInsetNavigationGroup.fitSystemNavigationWindow()
    }

    inner class BindingConfig{

        fun naviBack(){
            finish()
        }

        fun fullScreen(){
            logd("change fullScreen mode ${isFullScreen}")
            isFullScreen = !isFullScreen

//            val controller = WindowCompat.getInsetsController(window,mBinding.root)
//            controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
//            if (isFullScreen){
//                controller?.hide(WindowInsetsCompat.Type.statusBars())
//            }else{
//                controller?.show(WindowInsetsCompat.Type.statusBars())
//            }
            setFullScreenMode(isFullScreen)
        }

        fun updateNavigationBar(){
            isNavigationBar = !isNavigationBar
            setSystemNavigationBarEnable(isNavigationBar)
        }
    }
}