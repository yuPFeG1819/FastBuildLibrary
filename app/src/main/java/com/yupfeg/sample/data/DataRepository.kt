package com.yupfeg.sample.data

import com.yupfeg.remote.tools.handler.GlobalHttpResponseProcessor
import com.yupfeg.remote.tools.handler.RestApiException
import com.yupfeg.sample.data.api.WanAndroidApi
import com.yupfeg.sample.data.entity.WanAndroidArticleListResponseEntity
import com.yupfeg.sample.data.remote.wanAndroidApiDelegate

/**
 * 测试数据源
 * @author yuPFeG
 * @date
 */
class DataRepository {

    private val mWanAndroidApi : WanAndroidApi by wanAndroidApiDelegate()

    /**
     * 查询文章列表数据
     * - 挂起函数
     * @param pageIndex 分页页数，默认为1
     * */
    suspend fun queryArticleList(pageIndex : Int) : WanAndroidArticleListResponseEntity{
        val result = mWanAndroidApi.queryArticle(pageIndex)
        if (!GlobalHttpResponseProcessor.preHandleHttpResponse(result)){
            throw RestApiException(result.errorCode,result.errorMsg?:"")
        }
        return result
    }
}