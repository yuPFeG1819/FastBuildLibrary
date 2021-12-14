package com.yupfeg.base.widget.recyclerview.viewHolder

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.ext.setThrottleClickListener

/**
 * RecyclerView的viewHolder基类
 *
 * 使用委托接口来bind数据信息
 * @author yuPFeG
 * @date 2019/6/14.
 */
class BaseViewHolder private constructor(itemView: View, viewSize: Int = 0) :
    RecyclerView.ViewHolder(itemView) {
    /**
     * key为Integer时性能更佳的HashMap替代类，存放item中的view，
     * 通过resId作为key取出对应的view,减少findViewById()的使用
     */
    private val mViews: SparseArray<View> by lazy(LazyThreadSafetyMode.NONE) {
        return@lazy if (viewSize <= 0) {
            //默认创建大小为10的容器，一般足够使用了
            SparseArray()
        } else {
            SparseArray(viewSize)
        }
    }

    companion object {

        /**
         * 静态构造函数，创建ViewHolder
         * @param itemView itemView实例
         */
        @JvmStatic fun createNewInstance(itemView: View): BaseViewHolder {
            return BaseViewHolder(itemView)
        }

        /***
         * 静态构造函数，创建viewHolder
         * @param parent    父布局viewGroup
         * @param resId     item的布局文件id
         */
        @JvmStatic fun createNewInstance(parent: ViewGroup, @LayoutRes resId: Int): BaseViewHolder {
            val itemView = LayoutInflater.from(parent.context).
                inflate(resId, parent, false)
            return BaseViewHolder(itemView)
        }
    }

    //--------------------------------抽取常用方法-------------------------------
    /**
     * 根据控件id获取item中对应的控件
     * @param viewId    resId
     * @param <T>   view泛型
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> getViewByResId(@IdRes viewId: Int): T {
        var view = mViews.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as T
    }

    /**
     * 抽取常用为[View.setTag]设置控件tag
     * @param viewId resId
     * @param tag 要保存的tag对象
     */
    fun setTag(@IdRes viewId: Int, tag: Any): BaseViewHolder {
        val view = getViewByResId<View>(viewId)
        view.tag = tag
        return this
    }

    /**
     * 抽取常用为[TextView.setText]设置文本方法
     * @param viewId    resId
     * @param text      显示文本内容(为NULL则显示"")
     */
    fun setText(@IdRes viewId: Int, @Nullable text: String? = ""): BaseViewHolder {
        val textView = getViewByResId<TextView>(viewId)
        textView.text = text ?: ""
        return this
    }

    /**
     * 抽取常用为[TextView.setText]设置文本方法
     * @param viewId    resId
     * @param text      显示文本内容(为NULL则显示"")
     */
    fun setText(@IdRes viewId: Int, @Nullable text : CharSequence? = "") : BaseViewHolder{
        val textView = getViewByResId<TextView>(viewId)
        textView.text = text ?: ""
        return this
    }

    /**
     * 抽取常用为[TextView.setText]设置文本方法
     * @param viewId    resId
     * @param strId      显示文本内容id
     */
    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseViewHolder {
        val textView = getViewByResId<TextView>(viewId)
        textView.setText(strId)
        return this
    }

    /**
     * 抽取常用的[TextView.setTextColor]设置文本颜色
     * @param viewId resId
     * @param colorId 显示文本颜色id
     */
    fun setTextColor(@IdRes viewId: Int, @ColorRes colorId: Int): BaseViewHolder {
        val textView = getViewByResId<TextView>(viewId)
        textView.setTextColor(ContextCompat.getColor(itemView.context, colorId))
        return this
    }

    /**
     * 抽取常用的[TextView.setTextSize],设置文本大小
     * @param viewId resId
     * @param textSize 文本大小
     *
     */
    fun setTextSize(@IdRes viewId: Int, textSize : Float) : BaseViewHolder{
        val textView = getViewByResId<TextView>(viewId)
        textView.textSize = textSize
        return this
    }

    /**
     * 抽取常用的ImageView.setImageResource设置图片id
     * @param viewId viewId
     * @param drawableId 图片resId
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes drawableId: Int): BaseViewHolder {
        val imageView = getViewByResId<ImageView>(viewId)
        imageView.setImageResource(drawableId)
        return this
    }

    /**
     * 抽取常用的view.setVisibility设置控件显示
     * @param viewId resId
     * @param isVisibility 控件是否显示
     */
    fun setVisibility(@IdRes viewId: Int, isVisibility: Boolean): BaseViewHolder {
        val view = getViewByResId<View>(viewId)
        view.visibility = if (isVisibility) View.VISIBLE else View.GONE
        return this
    }

    /**
     * 抽取常用方法 view.setBackgroundResource设置控件背景drawable
     * @param viewId resId
     * @param drawableId 背景id
     */
    fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes drawableId: Int): BaseViewHolder {
        val view = getViewByResId<View>(viewId)
        view.setBackgroundResource(drawableId)
        return this
    }

    /**
     * 抽取为item的子view单独添加点击事件方法
     * @param viewId resId
     * @param listener 点击事件监听
     */
    fun setViewOnClickListener(@IdRes viewId: Int, listener: View.OnClickListener): BaseViewHolder {
        val view = getViewByResId<View>(viewId)
        view.setThrottleClickListener (onClickListener = listener)
        return this
    }

}