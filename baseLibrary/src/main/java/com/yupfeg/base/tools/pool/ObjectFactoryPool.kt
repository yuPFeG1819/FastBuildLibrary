package com.yupfeg.base.tools.pool

import androidx.core.util.Pools
import com.yupfeg.base.tools.pool.ObjectFactoryPool.Companion.DEFAULT_POOL_SIZE
import com.yupfeg.logger.ext.logd

/**
 * 快捷构建线程安全的对象缓存池
 * @param poolSize 缓存池大小
 * @param creator 对象池的新对象创建逻辑
 * @param reset 对象回收重置逻辑，默认可为null
 * */
@Suppress("unused")
fun <T> threadSafeObjectPools(
    poolSize: Int = DEFAULT_POOL_SIZE,
    creator : (()->T),
    reset : ((T)->Unit)? = null
) : ObjectFactoryPool<T>{
    return ObjectFactoryPool.createThreadSafe(poolSize,
        createFactory = object : ObjectFactoryPool.CreateFactory<T>{
            override fun create(): T {
                return creator.invoke()
            }
        },
        resetFactory = object : ObjectFactoryPool.ResetFactory<T>{
            override fun reset(instance: T) {
                reset?.invoke(instance)
            }
        }
    )
}

/**
 * 快捷构建，线程不安全的对象缓存池
 * @param poolSize 缓存池大小
 * @param creator 对象创建逻辑
 * @param reset 对象回收重置逻辑，默认可为null
 * */
@Suppress("unused")
fun <T> objectPools(
    poolSize: Int = DEFAULT_POOL_SIZE,
    creator: () -> T,
    reset: ((T) -> Unit)? = null
) : ObjectFactoryPool<T>{
    return ObjectFactoryPool.createUnSafe(poolSize,
        createFactory = object : ObjectFactoryPool.CreateFactory<T>{
            override fun create(): T {
                return creator.invoke()
            }
        },
        resetFactory = object : ObjectFactoryPool.ResetFactory<T>{
            override fun reset(instance: T) {
                reset?.invoke(instance)
            }
        }
    )
}

/**
 * 自定义对象池
 * * 修改自Glide库内部的`FactoryPools`，内部使用`Pools`构建对象池
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
        const val DEFAULT_POOL_SIZE = 5

        @Suppress("unused")
        @JvmStatic
        fun <T> createThreadSafe(
            poolSize : Int = DEFAULT_POOL_SIZE,
            createFactory : CreateFactory<T>,
            resetFactory: ResetFactory<T>? = null
        ) : ObjectFactoryPool<T>{
            return createInstance(Pools.SynchronizedPool(poolSize),createFactory,resetFactory)
        }

        fun <T> createUnSafe(
            poolSize : Int = DEFAULT_POOL_SIZE,
            createFactory : CreateFactory<T>,
            resetFactory: ResetFactory<T>? = null
        ) : ObjectFactoryPool<T>{
            return createInstance(Pools.SimplePool(poolSize),createFactory,resetFactory)
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


