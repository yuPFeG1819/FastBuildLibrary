package com.yupfeg.sample.ui

import android.graphics.Color
import android.os.Bundle
import com.yupfeg.base.view.activity.BaseActivity
import com.yupfeg.base.widget.ArcProgressBar
import com.yupfeg.logger.ext.logd
import com.yupfeg.sample.R

/**
 *
 * @author yuPFeG
 * @date
 */
class TestCustomViewActivity : BaseActivity(){
    override val layoutId: Int
        get() = R.layout.activity_test_custom_view

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(layoutId)

        val arcProgressView = findViewById<ArcProgressBar>(R.id.arc_progress_bar)
//        arcProgressView.setProgressColors(
//            intArrayOf(
//                Color.parseColor("#9162E4"),
//                Color.parseColor("#AAB6FE"),
//                Color.parseColor("#AAB6FE"),
//                Color.parseColor("#9162E4"),
//            ),
//            floatArrayOf(0f,0.6f,0.8f,1.0f)
//        )
//        arcProgressView.setProgressWithAnimate(15f,100f,2000)

        arcProgressView.setProgressWithColorChangeAnim(
            to = 100f,
            startColor = Color.YELLOW,
            endColor = Color.GREEN
        )

        arcProgressView.setOnProgressChangeListener(object : ArcProgressBar.OnProgressChangeListener{
            override fun onProgressChange(progress: Float, max: Int) {
                logd("进度条状态更新 : ${progress} / ${max}")
            }
        })
    }

    override fun initData() {
    }
}