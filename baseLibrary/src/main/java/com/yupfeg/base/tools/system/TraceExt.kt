package com.yupfeg.base.tools.system

import android.os.Debug
import android.os.Trace

/**
 * Trace代码插桩
 * @param sectionName 要出现在跟踪中的代码段的名称
 * @param block 实际执行逻辑
 */
inline fun traceSection(sectionName : String,block : ()->Unit){
    Trace.beginSection(sectionName)
    try {
        block()
    }finally {
        Trace.endSection()
    }
}

/**
 * TraceView代码插桩
 * @param fileName 生成的trace文件的保存文件名称,会在`mnt/sdcard/Android/data/包名/files`生成.trace文件
 * @param block 实际执行逻辑
 * */
inline fun tracingMethod(fileName : String, block: () -> Unit){
    Debug.startMethodTracing(fileName)
    try {
        block()
    }finally {
        Debug.stopMethodTracing()
    }
}