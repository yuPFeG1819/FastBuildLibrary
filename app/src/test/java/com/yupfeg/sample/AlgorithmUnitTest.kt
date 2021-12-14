package com.yupfeg.sample

import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * 算法相关单元测试
 * @author yuPFeG
 * @date
 */
class AlgorithmUnitTest {

    // <editor-fold desc="阶乘">

    @Test
    fun testFactorial(){
        var result1 : Long
        val time1 = measureNanoTime {
            result1 = getFactorial(20)
        }
        println("以递归的方式求阶乘 $result1 ($time1)μs")

        var result2 : Long
        val time2 = measureNanoTime {
            result2 = getFactorialByTailRecursion(20)
        }
        println("以尾递归的方式求阶乘 $result2 ($time2)μs")
        var result3 : Long
        val time3 = measureNanoTime {
            result3 = getFactorialByLoop(20)
        }
        println("以循环方式运行求阶乘：$result3 ($time3)μs")
    }

    /**
     * 递归求阶乘
     * * 任何大于等于1 的自然数n 阶乘表示方法：
     * n! = 1 * 2 * 3 * ... * （n-1） * n
     * * 0的阶乘
     * 0！ = 1
     * @param n 正整数
     * */
    private fun getFactorial(n : Long) : Long{
        if (n <= 0) return 1
        return n * getFactorial(n-1)
    }

    /**
     * 使用尾递归方式，求阶乘
     * * 任何大于等于1 的自然数n 阶乘表示方法：
     * n! = 1 * 2 * 3 * ... * （n-1） * n
     * * 0的阶乘
     * 0！ = 1
     * @param n 正整数
     * */
    private fun getFactorialByTailRecursion(n : Long) : Long{
        //利用tailrec关键字，将原本的递归函数调用，优化成迭代方式
        tailrec fun innerFactorial(n : Long,temp : Long) : Long{
            if (n <= 1) return temp
            return innerFactorial(n-1,temp * n)
        }
        return innerFactorial(n,1)
    }

    /**
     * 以循环的方式，求阶乘
     * * 任何大于等于1 的自然数n 阶乘表示方法：
     * n! = 1 * 2 * 3 * ... * （n-1） * n
     * * 0的阶乘
     * 0！ = 1
     * @param n 正整数
     * */
    private fun getFactorialByLoop(n : Long) : Long{
        var result = 1L
        if (n <= 0) return result
        for (i in (1..n)) {
            result *= i
        }
        return result
    }

    // </editor-fold>

    // <editor-fold desc="斐波那契数列">

    @Test
    fun testFibonacci(){
        val result1 : UInt
        val n = 10u
        val time1 = measureNanoTime {
            result1 = getFibonacciByTailRecursion(n)
        }
        println("尾递归方式获取斐波那契数列的值：$result1 on (${time1})μs")

        val result2 : UInt
        val time2 = measureNanoTime {
            result2 = getFibonacciByLoop(n)
        }
        println("循环方式获取斐波那契数列的值：$result2 on (${time2})μs")
    }

    /**
     * 以尾递归方式求斐波那契数列
     * * 数列从第3项开始，每一项都等于前两项之和
     * @param n 正整数
     * */
    private fun getFibonacciByTailRecursion(n : UInt) : UInt{
        /**
         * 内部运算
         * 利用tailrec关键字，将原本的递归函数调用，优化成迭代方式
         */
        tailrec fun innerFibonacci(n : UInt,a : UInt,b : UInt) : UInt{
            //前两项，都等于本身
            if (n < 2u) return b
            return innerFibonacci(n-1u,b,a+b)
        }
        return innerFibonacci(n,0u,1u)
    }

    /**
    * 以循环方式求斐波那契数列
    * * 数列从第3项开始，每一项都等于前两项之和
    * @param n 正整数
    * */
    private fun getFibonacciByLoop(n : UInt) : UInt{
       if (n < 2u) return n
       var a = 0u
       var b = 1u
       for(i in 2u..n){
           val temp = b
           b = a+b
           a = temp
       }
       return b
    }

//    private fun get

//    fun createFibonacciArray(n : UInt) : List<UInt>{
//
//    }

    // </editor-fold>
}