package com.yupfeg.sample

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * RxJava相关单元测试
 * @author yuPFeG
 * @date
 */
class RxJavaUnitTest {

    fun testObservable(){
        Observable.create<String> { emitter->
            val array = arrayOf("first","second","third")
            array.forEach {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }
            .subscribe{

            }
    }

    @Test
    fun testFlatMap(){
        var isComplete = false
        val startTime = System.currentTimeMillis()
        Observable.create<String> { emitter->
            val array = arrayOf("first","second","third")
            array.forEach {
//                if (it == "second") throw NullPointerException("test")
                emitter.onNext(it)
            }
            emitter.onComplete()
        }
            .delay(50,TimeUnit.MILLISECONDS)
            .flatMap {text->
                println("run flat map : $text ${System.currentTimeMillis()-startTime}ms")
                Observable.create<List<String>> {
                    val list = mutableListOf<String>()
                    list.add("$text __1")
                    list.add("$text __2")
                    list.add("$text __3")
                    it.onNext(list)
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<List<String>>{
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<String>) {
                    println("onNext : $t ${System.currentTimeMillis()-startTime}ms")
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                    isComplete = true
                }

            })
    }

    @Test
    fun testZip(){
        val observable1 = Observable.create<String> { emitter->
            val array = arrayOf("first","second","third")
            array.forEach {
                if (it == "second") throw NullPointerException("test")
                emitter.onNext(it)
            }
        }.delay(50,TimeUnit.MILLISECONDS)

        val observable2 = Observable.create<Int> { emitter->
            val array = arrayOf(1,3,5)
            array.forEach { emitter.onNext(it) }
        }.delay(100,TimeUnit.MILLISECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .publish()

        observable1
            .doOnSubscribe {
                println("run do on subscribe 1")
            }
            .startWithItem("11")
            .switchIfEmpty {
                it.onNext("empty value")
                it.onComplete()
            }
            .onErrorReturnItem("error return item")
            .retryWhen (ObservableRetryWithDelay(3,100,TimeUnit.MILLISECONDS))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: String) {
                    println("on Next $t")
                }

                override fun onError(e: Throwable) {
                    println("on Error $e")
                }

                override fun onComplete() {
                    println("on Complete")
                }

            })
//
        Thread.sleep(1000)
//        Observable.zip(observable1,observable2, { t1, t2 ->
//            "$t1 + $t2"
//        })
//            .subscribe {
//                println("on Next $it")
//            }
//        Thread.sleep(1000)
    }


    /**
     * [Observable]的延迟重试执行方法对象
     * @param maxRetryCount 最大重试次数
     * @param delay 重试延迟的时间
     * @param unit 延迟时间的单位
     * */
    private class ObservableRetryWithDelay(
        private val maxRetryCount : Int = 1,
        private val delay: Long = 1,
        private val unit : TimeUnit = TimeUnit.SECONDS
    )
        : Function<Observable<Throwable>, ObservableSource<*>> {
        /**当前重试次数*/
        private var retryCount : Int = 0

        override fun apply(errorObservable : Observable<Throwable>): ObservableSource<*> {
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Observable<Any>进行处理，
            // 此处使用flatMap操作符接收上游的数据
            return errorObservable.flatMap {throwable->
                if (++retryCount <= maxRetryCount){
                    //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                    return@flatMap Observable.timer(delay,unit)
                }
                //超出最大重试次数
                return@flatMap Observable.error<Exception>(throwable)
            }
        }

    }

    @Test
    fun testSubjects(){

        val publishSubject = PublishSubject.create<String>()
        publishSubject.onNext("1")
        publishSubject.toFlowable(BackpressureStrategy.LATEST)

        val behaviorSubject = BehaviorSubject.createDefault("1")

        val result = behaviorSubject.value
    }
}