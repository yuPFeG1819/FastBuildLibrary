package com.yupfeg.sample.ui

import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.sample.domain.OtherUseCase
import com.yupfeg.sample.domain.TestUseCase

/**
 *
 * @author yuPFeG
 * @date
 */
class MainViewModel : BaseViewModel(){

    val testUseCase = TestUseCase()
    val otherUserCase = OtherUseCase()

    init {
        addUseCase(testUseCase)
        addUseCase(otherUserCase)
    }

}