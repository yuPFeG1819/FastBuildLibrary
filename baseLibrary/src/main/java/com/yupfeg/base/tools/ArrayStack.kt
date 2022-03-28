package com.yupfeg.base.tools

import java.util.*

/**
 * 以空数组双端队列构成的，线程不安全的栈结构，Stack类的简单替代，避免同步锁操作
 * - Stack，包含了在任何位置添加或者删除元素的方法，这些不是栈应该有的方法，所以需要屏蔽掉这些无关的方法。
 * - 不允许null元素
 * @author yuPFeG
 * @date 2022/03/24
 */
@Suppress("unused")
class ArrayStack<E> : UnSafeStack<E>, MutableIterable<E> {
    private val mDequeue = ArrayDeque<E>()

    //从双端队列的head推入
    override fun push(e: E) = mDequeue.push(e)

    //从双端队列的head移出
    override fun pop(): E? = mDequeue.pop()

    //检索获取双端队列的tail元素
    override fun peek(): E? = mDequeue.peek()

    override val size: Int
        get() = mDequeue.size

    override val isEmpty: Boolean
        get() = mDequeue.isEmpty()

    val isNotEmpty : Boolean = !mDequeue.isEmpty()

    /**
     * 校验是否包含指定元素
     * */
    fun contains(e : E) : Boolean = mDequeue.contains(e)

    /**
     * 检索但不删除此双端队列的最后一个元素(最新添加的元素)，如果此双端队列为空，则返回null
     * */
    fun peekLast() : E? = mDequeue.peekLast()

    /**
     * 删除最旧一项数据
     * - 该数据结构是作为栈来使用，只能从栈尾添加数据
     * */
    fun removeFirst(){
        mDequeue.removeFirst()
    }

    /**
     * 移除第一个出现指定元素，从第一个（头，最新入栈）向最后一个（尾，最旧入栈）进行遍历检索，然后移除最先出现的指定项
     * @return 如果成功删除则返回true
     * */
    fun remove(e : E) : Boolean = mDequeue.removeFirstOccurrence(e)

    /**
     * 正序迭代器，元素将按从第一个（头索引，最新一个）到最后一个（尾索引，最旧一个）的顺序返回。
     * - 栈结构是只能从一端添加与取出的，迭代也是从插入段（头索引）开始迭代
     * @return 返回的是允许删除功能的迭代器
     * */
    override fun iterator(): MutableIterator<E> {
        return mDequeue.iterator()
    }

}

/**
 * 线程不安全的栈类型结构
 * - 满足后进先出（LIFO）定义
 * */
internal interface UnSafeStack<E> {
    /**将元素添加到栈上，压到栈尾*/
    fun push(e: E)
    /**从堆栈中弹出栈尾的一个元素，取出并删除最后一个元素，如果没有则为null*/
    fun pop(): E?
    /**检索堆栈的第一个元素，但不会删除元素，没有则为null*/
    fun peek(): E?
    /**栈的容量大小*/
    val size : Int
    /**校验栈空间是否为空*/
    val isEmpty : Boolean
}