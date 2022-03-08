package com.yupfeg.sample.domain

import com.yupfeg.base.domain.LifecycleUseCase
import com.yupfeg.base.domain.UseCase
import com.yupfeg.logger.ext.logd

/**
 *
 * @author yuPFeG
 * @date
 */
class OtherUseCase : UseCase(){

//    override var isPrintDebugLifecycleLog: Boolean = false

    fun queryData(){
        logd("other query data")
    }
}