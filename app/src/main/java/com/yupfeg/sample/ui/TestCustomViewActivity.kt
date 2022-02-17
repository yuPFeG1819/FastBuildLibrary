package com.yupfeg.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Button
import com.yupfeg.base.tools.anim.*
import com.yupfeg.base.tools.anim.evalutor.LinearColorEvaluator
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
    private var arcProgressView : ArcProgressBar? = null
    private var btn : Button? = null

    override val layoutId: Int
        get() = R.layout.activity_test_custom_view

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(layoutId)

        arcProgressView = findViewById<ArcProgressBar>(R.id.arc_progress_bar)
        btn = findViewById<Button>(R.id.btn_test_start_anim).apply {
            this.setOnClickListener {
                startAnim()
            }
        }
//        arcProgressView.setProgressColors(
//            intArrayOf(
//                Color.parseColor("#9162E4"),
//                Color.parseColor("#AAB6FE"),
//                Color.parseColor("#AAB6FE"),
//                Color.parseColor("#9162E4"),
//            ),
//            floatArrayOf(0f,0.6f,0.8f,1.0f)
//        )
//        arcProgressView.setProgressWithAnimate(0f,100f,2000)

        arcProgressView?.setOnProgressChangeListener(object : ArcProgressBar.OnProgressChangeListener{
            override fun onProgressChange(progress: Float, max: Int) {
                logd("进度条状态更新 : ${progress} / ${max}")
            }
        })

    }

    override fun initData() {

    }

    fun startAnim(){
        objectAnim {
            target = arcProgressView
            addIntValues("progressColor", LinearColorEvaluator(), Color.RED, Color.GREEN)
            addFloatValues("progress",null,0f,90f)
            interpolator = LinearInterpolator()
            duration = 2000
        }.start()

        animSet {
            interpolator = LinearInterpolator()

            val animWrapper1 = playObjectAnim {
                target = btn!!
                alpha = floatArrayOf(0f,1f)
                duration = 500
            }
            val anim2 = objectAnim {
                target = btn!!
                translationX = floatArrayOf(-200f, 200f)
                duration = 600
            }

            val anim3 = objectAnim {
                target = btn!!
                duration = 1000
                rotation = floatArrayOf(0f,1080f)
            }

            animWrapper1 before anim2
            playTogether(anim2,anim3)
        }.start()
    }
}