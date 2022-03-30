package com.yupfeg.sample.ui

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *
 * @author yuPFeG
 * @date
 */
class TestWindowInsetViewModel : ViewModel(){

    val onTestInputTest = ObservableField("")

    val titleLiveData : LiveData<String>
        get() = mTitleLiveData

    private val mTitleLiveData = MutableLiveData<String>("测试WindowInset")
}