package com.yupfeg.sample.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.executor.ExecutorProvider
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 * @author yuPFeG
 * @date
 */
class TestListViewModel : BaseViewModel(){

    val titleName : LiveData<String>
        get() = mTitleName

    private val mTitleName = MutableLiveData<String>("测试列表")


    private val mListStateFlow = MutableStateFlow<List<Any>>(emptyList())

    val listStateFlow : StateFlow<List<Any>> = mListStateFlow.asStateFlow()

    fun getTestListData(){
        viewModelScope.launch(ExecutorProvider.ioExecutor.asCoroutineDispatcher()) {
            val list = mutableListOf<Any>()
            for (i in 0 until 20){
                val item = TestListItemBean().apply {
                    id = i + 111L
                    content = "测试阿斯拉达就按死了控件的拉克丝就打了卡时间打老实交代阿临时卡接单啦开始减肥坷垃神教阿赫反馈路径安徽的是${i}"
                    time = "10${i}分钟前"
                    userName = "测试标题文办名称${i}"
                    imgDates = if (i > 0 && i % 2 == 0) List(15){ "测试222${it}${i}" }
                    else List(i+1) {"1222${i}"}
                    singleImgWidth = 1000
                    singleImgHeight = 800
                }
                list.add(item)
            }
            mListStateFlow.emit(list)
        }
    }
}