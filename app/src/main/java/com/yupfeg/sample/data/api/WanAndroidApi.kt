package com.yupfeg.sample.data.api

import com.yupfeg.sample.data.entity.WanAndroidArticleListResponseEntity
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 测试
 * @author yuPFeG
 * @date
 */
interface WanAndroidApi {
    /**
     * 基于kotlin 协程，获取WanAndroid的文章列表
     * @param pageNum 列表分页页数
     * */
    @GET("article/list/{pageNum}/json")
    suspend fun queryArticle(
        @Path("pageNum") pageNum: Int
    ) : WanAndroidArticleListResponseEntity
}