package com.yupfeg.sample.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.executor.ExecutorProvider
import com.yupfeg.sample.data.DataRepository
import com.yupfeg.sample.domain.WanAndroidArticleUseCase
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
class TestListViewModel(dataRepo : DataRepository = DataRepository()) : BaseViewModel(){

    val titleName : LiveData<String>
        get() = mTitleName

    private val mTitleName = MutableLiveData("测试列表")

    val articleUseCase = WanAndroidArticleUseCase(dataRepo)

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
                    imgDates = if (i == 0 ) List(1){ "测试222${it}${i}" }
                    else List(i+1) {"测试1222${i}"}
                    if (imgDates?.size?:0 == 1){
                        singleImgWidth = 1000
                        singleImgHeight = 800
                    }
                }
                list.add(item)
            }
            mListStateFlow.emit(list)
        }
    }

}