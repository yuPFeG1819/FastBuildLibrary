package com.yupfeg.sample.domain

import com.yupfeg.base.domain.LifecycleUseCase
import com.yupfeg.logger.ext.logd

/**
 *
 * @author yuPFeG
 * @date
 */
class OtherUseCase : LifecycleUseCase(){

    override var isPrintDebugLifecycleLog: Boolean = false

    fun queryData(){
        logd("other query data")
    }
}