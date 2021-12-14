package com.yupfeg.base.tools.pool

import androidx.core.util.Pools
import com.yupfeg.logger.ext.logd

/**
 * 自定义对象池
 * * 修改自Glide库内部的FactoryPools
 * @author yuPFeG
 * @date 2020/12/17
 */
@Suppress("unused")
class ObjectFactoryPool<T> private constructor(
    private val mObjectPool: Pools.Pool<T>,
    private val mCreateFactory: CreateFactory<T>,
    private val mResetFactory: ResetFactory<T>?
) : Pools.Pool<T> {

    companion object{
        private const val TAG = "FactoryObjectPool"
        private const val DEFAULT_POOL_SIZE = 5

        @Suppress("unused")
        @JvmStatic
        fun <T> createThreadSafe(
            poolSize : Int = DEFAULT_POOL_SIZE,
            createFactory : CreateFactory<T>,
            resetFactory: ResetFactory<T>? = null
        ) : ObjectFactoryPool<T>{
            return createInstance(Pools.SynchronizedPool(poolSize),createFactory,resetFactory)
        }

        @Suppress("unused")
        @JvmStatic
        fun <T> createInstance(
            pool : Pools.Pool<T>,
            createFactory : CreateFactory<T>,
            resetFactory: ResetFactory<T>? = null
        ) : ObjectFactoryPool<T>{
            return ObjectFactoryPool(pool,createFactory, resetFactory)
        }
    }

    /**
     * Creates new instances of the given type.
     *
     * @param <T> The type of Object that will be created.
     * */
    interface CreateFactory<T> {
        fun create(): T
    }

    /**
     * Resets state when objects are returned to the pool.
     *
     * @param <T> The type of Object that will be reset.
     * */
    @Suppress("SpellCheckingInspection")
    interface ResetFactory<T> {
        fun reset(instance : T)
    }

    /**
     * Allows additional verification to catch errors caused by using objects while they are in an
     * object pool.
     */
    @Suppress("SpellCheckingInspection")
    abstract class Poolable {
        @Volatile
        var isReleased = false
    }

    override fun acquire(): T {
        return mObjectPool.acquire()?:run {
            val instance = mCreateFactory.create()
            (instance as? Poolable)?.isReleased = false
            logd(TAG, "ObjectFactoryPool Created new " + instance!!::class.java)
            return@run instance
        }
    }

    override fun release(instance: T): Boolean {
        instance?: return false
        /*isRecycled*/
        (instance as? Poolable)?.isReleased = true
        mResetFactory?.reset(instance)
        return mObjectPool.release(instance)
    }

}


