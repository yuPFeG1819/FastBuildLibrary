package com.yupfeg.sample.ui

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import androidx.databinding.ObservableField
import com.yupfeg.base.tools.spannable.append
import com.yupfeg.base.tools.ext.toPx
import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.livedata.EventLiveData
import com.yupfeg.livedata.MutableEventLiveData
import com.yupfeg.sample.domain.TestUseCase

/**
 *
 * @author yuPFeG
 * @date
 */
class TestResultViewModel : BaseViewModel() {

    val testUseCase = TestUseCase()

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
                setTextSize(12.toPx())
            }
            append("ResultAPI "){
                setTextSize(18.toPx())
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