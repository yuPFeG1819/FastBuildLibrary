package com.yupfeg.sample.ui

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yupfeg.base.viewmodel.BaseViewModel

/**
 *
 * @author yuPFeG
 * @date
 */
class TestWindowInsetViewModel : BaseViewModel(){

    val onTestInputTest = ObservableField("")

    val titleLiveData : LiveData<String>
        get() = mTitleLiveData

    private val mTitleLiveData = MutableLiveData<String>("测试WindowInset")
}