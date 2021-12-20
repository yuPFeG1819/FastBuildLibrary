package com.yupfeg.rxjavasupport.dialog

import android.content.Context
import android.view.View
import com.yupfeg.base.tools.pool.GlobalLocalThreadPool
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yupfeg.logger.ext.logw
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 字符串格式的条件选择弹窗基类
 * * 支持三级联动的字符串条件选择滚轮
 * //TODO 后续替换为更好的实现方案
 * @author yuPFeG
 * @date 2020/01/21
 */

@Suppress("unused")
abstract class BaseStringThirdLinkedDialog constructor(protected val mContext: Context){
    /**第一级滚轮数据集合*/
    protected val mFirstItems = mutableListOf<String>()
    /**第二级滚轮数据集合*/
    protected val mSecondItems = mutableListOf<List<String>>()
    /**第三级滚轮数据集合*/
    protected val mThirdItems = mutableListOf<List<List<String>>>()
    /**字符串的三级联动滚轮控件*/
    protected var mOptionsPickerView : OptionsPickerView<String>?= null

    /**
     * 显示三级联动条件选择弹窗
     */
    open fun showOptionsDialog(){
        if (mOptionsPickerView == null){
            mOptionsPickerView = createOptionsPickerView { options1, options2, options3, _ ->
                getSelectOptionsDataCallBack(
                    options1 = options1,
                    options2 = options2,
                    options3 = options3
                )
            }
            firstInitPickerData()
        }else{
            mOptionsPickerView?.setPicker(mFirstItems,mSecondItems,mThirdItems)
            mOptionsPickerView?.show(true)
        }

    }

    /**第一次启动时初始化滚轮展示的数据集*/
    protected open fun firstInitPickerData(){
        Completable.fromAction { initOptionsData() }
            //step1:切换上游处于子线程
            .subscribeOn(Schedulers.from(GlobalLocalThreadPool.executorService))
            //step2:在订阅发起时，清空原有数据
            .doOnSubscribe { clearPickerData() }
            //step3:切换下游处于主线程
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mOptionsPickerView?.setPicker(mFirstItems,mSecondItems,mThirdItems)
                    mOptionsPickerView?.show(true)
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(error: Throwable) {
                    logw("TimePickerDialog",error)
                }
            })
    }

    /**创建字符串类型三级联动滚轮实例，用于子类拓展使用*/
    protected open fun createOptionsPickerView(listener : OnOptionsSelectListener) : OptionsPickerView<String>{
        return OptionsPickerBuilder(mContext,listener)
                //设置自定义弹窗布局
                .setLayoutRes(customLayoutId) {
                    v -> v?.let { initOptionsDialogView(it) }
                }
                //滚轮内容文本大小
                .setContentTextSize(18)
                .setOutSideCancelable(true)
                //切换时是否还原，设置默认选中第一项。
                .isRestoreItem(true)
                .build()
    }

    /**设置自定义条件选择弹窗布局*/
    protected abstract val customLayoutId :  Int

    /**初始化自定义条件选择弹窗*/
    protected abstract fun initOptionsDialogView(view : View)

    /**
     * 初始化条件弹窗的数据
     * PS：该方法执行在子线程，不要做修改UI的操作
     * */
    protected abstract fun initOptionsData()

    /**
     * 从三级联动滚轮获取已选择的数据
     *
     * 子类调用[OptionsPickerView.returnData]会触发该回调
     * @param options1 第一级滚轮选择index
     * @param options2 第二级滚轮选择index
     * @param options3 第三级滚轮选择index
     */
    protected abstract fun getSelectOptionsDataCallBack(options1 : Int, options2 : Int, options3 : Int)

    /**赋值第一级滚轮数据*/
    protected fun putFirstPickerData(list : List<String>){
        mFirstItems.addAll(list)
    }

    /**获取第一级滚轮数据*/
    protected fun getFirstPickerItem(index : Int) : String{
        if (index > mFirstItems.size) return ""
        return mFirstItems[index]
    }

    /**赋值第二级滚轮数据*/
    protected fun putSecondPickerData(list : List<List<String>>){
        mSecondItems.addAll(list)
    }

    /**赋值第三级滚轮数据*/
    protected fun putThirdPickerData(list: List<List<List<String>>>){
        mThirdItems.addAll(list)
    }

    /**清空原有数据*/
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun clearPickerData(){
        if (mFirstItems.isNotEmpty()) mFirstItems.clear()
        if (mSecondItems.isNotEmpty()) mSecondItems.clear()
        if (mThirdItems.isNotEmpty()) mThirdItems.clear()
    }

    /**关闭三级联动弹窗*/
    protected fun dismissDialog(){
        mOptionsPickerView?.dismiss()
    }

    /**确认滚轮当前选择数据，并关闭弹窗*/
    protected fun confirmDialog(){
        mOptionsPickerView?.returnData()
        mOptionsPickerView?.dismiss()
    }
}