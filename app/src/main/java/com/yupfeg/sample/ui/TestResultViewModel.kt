package com.yupfeg.sample.ui

import androidx.lifecycle.ViewModel
import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.sample.domain.TestUseCase

/**
 *
 * @author yuPFeG
 * @date
 */
class TestResultViewModel : BaseViewModel() {

    val mTestUseCase = TestUseCase()

    init {
        addUseCase(mTestUseCase)
    }
}