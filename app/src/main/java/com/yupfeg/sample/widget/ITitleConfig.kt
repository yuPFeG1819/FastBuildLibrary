package com.yupfeg.sample.widget

import androidx.lifecycle.LiveData

/**
 *
 * @author yuPFeG
 * @date
 */
interface ITitleConfig {

    fun back()

    val titleName : LiveData<String>
}