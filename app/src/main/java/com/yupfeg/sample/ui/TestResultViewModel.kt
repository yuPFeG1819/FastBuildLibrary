package com.yupfeg.sample.ui

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.yupfeg.base.tools.spannable.append
import com.yupfeg.base.tools.ext.dipToPx
import com.yupfeg.livedata.EventLiveData
import com.yupfeg.livedata.MutableEventLiveData

/**
 *
 * @author yuPFeG
 * @date
 */
class TestResultViewModel : ViewModel() {

    val testSpannableString = ObservableField<CharSequence>()
    val textClickMovementMethod: MovementMethod = LinkMovementMethod.getInstance()
    val testSpannableHighLightColor : Int = Color.TRANSPARENT

    val spannableClickEvent : EventLiveData<String>
        get() = mSpannableClickEvent
    private val mSpannableClickEvent = MutableEventLiveData<String>()

    init {
        initTestSpannableString()
    }

    private fun initTestSpannableString(){
        val spannableString = SpannableStringBuilder().apply {
            append("测试") {
                textColor = Color.DKGRAY
                setTextSize(12.dipToPx())
            }
            append("ResultAPI "){
                setTextSize(18.dipToPx())
                textColor = Color.BLUE
                setOnClick {
                    mSpannableClickEvent.value = "spannableClick"
                }
            }
            append("返回到原始页面"){
                textColor = Color.GREEN
            }
        }
        testSpannableString.set(spannableString)
    }
}