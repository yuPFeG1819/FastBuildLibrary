package com.yupfeg.sample

import org.junit.Test
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureNanoTime

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

    // <editor-fold desc="拓扑排序">

    /**
     * 测试拓扑排序
     * - 现在你总共有 n 门课需要选，记为 0 到 n-1。
     * - 在选修某些课程之前需要一些先修课程。 例如，想要学习课程 0 ，你需要先完成课程 1 ，我们用一个匹配来表示他们: [0,1]
     *
     *  [leetCode原题 210](https://leetcode-cn.com/problems/course-schedule-ii/)
     *  输入：numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
     *  输出： [0,2,1,3]
     *  解释：总共有 4 门课程。要学习课程 3，你应该先完成课程 1 和课程 2。并且课程 1 和课程 2 都应该排在课程 0 之后。
     *  因此，一个正确的课程顺序是[0,1,2,3] 。另一个正确的排序是[0,2,1,3] 。
     *  [解题思路](https://juejin.cn/post/6873233326186954765)
     */
    @Test
    fun testTopologicalSort(){
        /**
         * 构建依赖关系1
         * C1 - 无依赖
         * C2 - 依赖C1
         * C3 - 依赖C1
         * C4 - 依赖C2、C3
         */
//        val count = 4
//        val list = mutableListOf<Array<Int>>()
//        list.add(arrayOf(1,0))
//        list.add(arrayOf(2,0))
//        list.add(arrayOf(3,1))
//        list.add(arrayOf(3,2))


        /**
         * 构建依赖关系2
         * C1 - 无依赖
         * C2 - 无依赖
         * C3 - 无依赖
         * C4 - 依赖C1、C2
         * C5 - 依赖C2、C3
         * C6 - 依赖C4、C5
         * */
//        val count = 6
//        val list = mutableListOf<Array<Int>>()
//        list.add(arrayOf(3,0))
//        list.add(arrayOf(3,1))
//        list.add(arrayOf(4,1))
//        list.add(arrayOf(4,2))
//        list.add(arrayOf(5,3))
//        list.add(arrayOf(5,4))

        /**
         * 构建依赖关系3
         *  C1 - 无依赖，
         *  C2 - 无依赖
         *  C3 - 依赖C1、C2
         *  C4 - 依赖C3、C5
         *  C5 - 依赖C2
         *  C6 - 依赖C4、C5
         *  C7 - 依赖C4、C9
         *  C8 - 依赖C1
         *  C9 - 依赖C8
         *  求解一种可行的顺序，能够让我把所有课都学了。
         *  结果 :
         *  BFS : [0, 1, 7, 2, 4, 8, 3, 5, 6]
         *  DFS : [1, 4, 0, 7, 8, 2, 3, 6, 5]
         * */
        val count = 9
        val list = mutableListOf<Array<Int>>()
        list.add(arrayOf(2,0)) //c1 -> c3
        list.add(arrayOf(2,1)) //c2 -> c3
        list.add(arrayOf(3,2)) //c3 -> c4
        list.add(arrayOf(3,4)) //c5 -> c4
        list.add(arrayOf(4,1)) //c2 -> c5
        list.add(arrayOf(5,3)) //c4 -> c6
        list.add(arrayOf(5,4)) //c5 -> c6
        list.add(arrayOf(6,3)) //c4 -> c7
        list.add(arrayOf(6,8)) //c9 -> c7
        list.add(arrayOf(7,0)) //c1 -> c8
        list.add(arrayOf(8,7)) //c8 -> c9

        val nodeArray = list.toTypedArray()
        val builder = StringBuilder()
        builder.append("input : ")
        for (ints in nodeArray) {
            builder.append("[${ints[0]} , ${ints[1]}] ,")
        }
        println(builder.toString())

        //广度优先搜索算法
        val sortList = sortByBFS(count,nodeArray)

        builder.clear()
        builder.append("sortFromBFS output : [")
        for (i in sortList) {
            builder.append("$i ,")
        }
        builder.deleteAt(builder.length-1)
        builder.append("]")
        println(builder.toString())

        //深度优先搜索算法
        val dfsSortList = sortByDFS(count,nodeArray)

        builder.clear()
        builder.append("sortFromDFS output : [")
        for (i in dfsSortList) {
            builder.append("$i ,")
        }
        builder.deleteAt(builder.length-1)
        builder.append("]")
        println(builder.toString())
    }

    /**
     * 使用广度优先检索(BFS)算法进行拓扑排序
     * - 从根结点开始，沿着树的宽度遍历树的结点。如果所有结点均被访问，则算法中止。
     * 由近到远的访问方式 :
     * 1. 计算所有节点入度，将入度为0的节点放入队列
     * 2. 遍历入度为0的节点队列
     * 3. 通知所有依赖该节点的入度-1，如果节点为0，则放入节点队列继续遍历
     * @param count 节点数量
     * @param array 节点依赖关系，内部数组（0：表示目标课程索引，1:依赖的前置课程索引）
     * */
    private fun sortByBFS(count : Int, array : Array<Array<Int>>) : Array<Int>{
        if (count <= 0) return emptyArray()
        //1. 计算所有节点的入度（是否存在依赖）
        val inDegreeArray : Array<Int> = Array(count){0}
        for (depends in array){
            inDegreeArray[depends[0]]++
        }

        val nodeQueue : Queue<Int> = ArrayDeque()
        //2. 检索所有入度为0的节点
        for (index in inDegreeArray.indices) {
            if (inDegreeArray[index] == 0){
                //将依赖数（入度）为0的节点添加队列（先进先出）
                nodeQueue.offer(index)
            }
        }

        val result = ArrayList<Int>()
        //3. 遍历所有入度为0的节点
        while (nodeQueue.isNotEmpty()){
            val currNodeIndex = nodeQueue.poll()
            currNodeIndex?:break
            //添加节点到排序后的集合
            result.add(currNodeIndex)
            //4. 遍历所有节点的依赖节点
            for (dependArray in array) {
                if (dependArray[1] == currNodeIndex){
                    //对应节点的依赖节点包含当前执行节点，则将对应节点入度-1
                    if (--inDegreeArray[dependArray[0]] == 0){
                        //5. 对应节点入度为0,则添加到执行队列
                        nodeQueue.offer(dependArray[0])
                    }
                }
            }
        }

        //校验排序后节点数量，如果不相等，则表示有环，不是有向无环图
        if (result.size != count) return emptyArray()
//        check (result.size == count){
//            "task list exist circle,check code input : $array result : $result"
//        }
        return Array(result.size){i-> result[i] }
    }

    /**
     * 使用深度优先检索(DFS)算法进行拓扑排序
     * - 对每一个可能的分支路径深入到不能再深入为止，向上回溯而且每个结点只能访问一次
     *
     * 1. 指定一点为顶点，进行标记，并查找该节点的任意一个相邻节点。
     * 2. 若该相邻节点未被访问，则对其进行标记，并进入递归，查找它的未被标记访问的邻接节点；若该节点已被访问标记，
     * 则回退到上级节点，查找它未被标记访问的邻接子节点，再进入递归，直到与起点相通的全部顶点都被标记访问为止。
     * 3. 若所有节点都被标记访问，就结束；反之，如果还有节点未被访问，
     * 则需要以该节点为顶点进行下一步的递归查找，直到所有点都被标记访问。
     *
     * [DFS算法思路](https://juejin.cn/post/6844903894414262280)
     * @param count 节点数量
     * @param array 节点依赖关系，内部数组（0：表示目标课程索引，1:依赖的前置课程索引）
     * */
    private fun sortByDFS(count : Int,array : Array<Array<Int>>) : Array<Int>{
        if (count == 0) return emptyArray()
        //1. 建立邻接矩阵数组
        val graphArray = Array<MutableList<Int>>(count){
            mutableListOf()
        }
        for (depends in array){
            //在每个被依赖的节点索引上添加其子节点
            graphArray[depends[1]].add(depends[0])
        }
        //2. 初始化建立所有节点的访问状态，访问过了标记：-1，正在访问标记：1，还未访问标记：0
        val visitStatusArray = Array(count){0}
        //3. 缓存已访问顺序的栈（以双端队列作为栈）
        val visitedStack : Deque<Int> = ArrayDeque()

        //4. 开启DFS节点遍历
        for (i in 0 until count){
            if (!dfsGraph(graphArray,visitStatusArray,i,visitedStack)) {
                //如果返回false表示存在环，重复访问，返回空数组
//                throw RuntimeException("task list exist circle,check code")
                return emptyArray()
            }
        }

        return Array(count){
            visitedStack.pop() //取出栈的第一个访问节点元素
        }
    }

    /**
     * 使用深度优先算法进行检索
     * @param graphArray
     * @param visitedStatusArray 所有节点的访问状态，访问过了标记：-1，正在访问标记：1，还未访问标记：0
     * @param nodeIndex 节点索引
     * @param visitedStack 已访问权限
     * @return true - 表示正常执行dfs访问，false - 存在环，重复访问同一个节点
     */
    private fun dfsGraph(
        graphArray : Array<MutableList<Int>>,
        visitedStatusArray : Array<Int>,
        nodeIndex : Int,
        visitedStack : Deque<Int>
    ) : Boolean{
        //如果该节点处于正在访问状态，说明存在环
        if (visitedStatusArray[nodeIndex] == 1) return false
        //已访问过，则不需要继续向下访问
        if (visitedStatusArray[nodeIndex] == -1) return true

        //标记为正在访问
        visitedStatusArray[nodeIndex] = 1
        //5. dsf遍历访问其子节点
        for (childNodeIndex in graphArray[nodeIndex]) {
            //6. 子节点还未访问过，则向下访问该节点
            if (visitedStatusArray[childNodeIndex] == 0){
                if (!dfsGraph(graphArray,visitedStatusArray, childNodeIndex, visitedStack)) {
                    //如果子节点存在环，则继续向上反馈
                    return false
                }
            }
        }
        //标记该节点为已访问
        visitedStatusArray[nodeIndex] = -1
        //7.添加该节点到访问顺序栈
        visitedStack.push(nodeIndex)
        return true
    }


    // </editor-fold>


    // <editor-fold desc="多线程">



    // </editor-fold>
}