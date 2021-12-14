package com.yupfeg.sample.ui

import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.sample.domain.OtherUseCase
import com.yupfeg.sample.domain.TestUseCase
import io.reactivex.rxjava3.core.Observable

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

    fun test(){
        Observable.create<String> {
            it.onNext("1")
        }.subscribe {

        }

    }

    override fun onCleared() {
        super.onCleared()
    }
}