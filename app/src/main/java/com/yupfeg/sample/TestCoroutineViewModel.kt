package com.yupfeg.sample

import androidx.lifecycle.viewModelScope
import com.yupfeg.base.viewmodel.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.callbackFlow

/**
 *
 * @author yuPFeG
 * @date
 */
class TestCoroutineViewModel : BaseViewModel(){


    fun testNewCoroutine(){
        val deferred = viewModelScope.async {
            //async创建的新协程，其中CoroutineContext

            val job = launch {

            }
        }

        val deferred2 = viewModelScope.async {

        }

        callbackFlow<Int> {  }

    }
}