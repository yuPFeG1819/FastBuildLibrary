package com.yupfeg.sample.domain

import com.yupfeg.base.data.dto.DataStateResult
import com.yupfeg.base.domain.UseCase
import com.yupfeg.executor.ExecutorProvider
import com.yupfeg.logger.ext.logd
import com.yupfeg.remote.tools.handler.GlobalHttpResponseProcessor
import com.yupfeg.sample.data.DataRepository
import com.yupfeg.sample.data.entity.WanAndroidArticleListResponseEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 玩Android的文章业务用例
 * @param dataRepo 数据源从外部传入，便于做单元测试
 * @author yuPFeG
 * @date 2022/03/04
 */
class WanAndroidArticleUseCase(private val dataRepo : DataRepository) : UseCase(){

    fun queryArticleList(pageIndex : Int){
        useCaseCoroutineScope.launch {
            performQueryArticleList(pageIndex)
                .collect {
                    reducerViewStateFromDataResult(it)
                }
        }
    }

    private fun performQueryArticleList(
        pageIndex: Int
    ) : Flow<DataStateResult<WanAndroidArticleListResponseEntity>>{
        return flow {
            //在请求发起前，先设置loading状态
            emit(DataStateResult.loading())
            val result = dataRepo.queryArticleList(pageIndex)
            logd("原始文章列表数据：${result}")
            emit(DataStateResult.success(result))
        }
            //指定上游数据流所在协程上下文
            .flowOn(ExecutorProvider.ioExecutor.asCoroutineDispatcher())
            //捕获所有上游抛出的异常
            .catch {error->
                if (error is CancellationException) throw error
                GlobalHttpResponseProcessor.handleHttpError(error)
                emit(DataStateResult.failure(error))
            }
    }

    private fun reducerViewStateFromDataResult(
        dataResult : DataStateResult<WanAndroidArticleListResponseEntity>
    ){
        when(dataResult){
            is DataStateResult.Loading->{
                logd("处于loading状态")
            }

            is DataStateResult.Failure->{
                logd("获取文章列表数据失败 ${dataResult.error}")
            }

            is DataStateResult.Success<WanAndroidArticleListResponseEntity>->{
                logd("获取文章列表数据成功回调")
            }
            else -> {}
        }
    }
}