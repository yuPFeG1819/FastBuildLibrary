package com.yupfeg.sample.domain

import com.yupfeg.base.domain.LifecycleUseCase
import com.yupfeg.logger.ext.logd

/**
 *
 * @author yuPFeG
 * @date
 */
class TestUseCase : LifecycleUseCase(){

    override var isPrintDebugLifecycleLog: Boolean = true
    fun queryTestData(){
        logd("测试")
    }

}