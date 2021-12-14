package com.yupfeg.base.tools.inputfilter

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import java.util.regex.Pattern


/**
 * 过滤用户输入只能为金额格式
 * @author yuPFeG
 * @date 2019/10/25
 */
@Suppress("unused")
class AmountInputFilter(private val allowPoint : Boolean = true,
                        private val maxAmount : Long = DEF_MAX_AMOUNT) : InputFilter {
    companion object{
        //默认输入的最大金额
        private const val DEF_MAX_AMOUNT : Long = 9999
        //小数点后的位数
        private const val POINTER_LENGTH = 2
        private const val POINTER = "."
        private const val ZERO = "0"
    }

    private val mPattern: Pattern by lazy(LazyThreadSafetyMode.NONE){
        Pattern.compile("([0-9]|\\.)*")
    }

    private val mNotPointPattern : Pattern by lazy(LazyThreadSafetyMode.NONE){
        Pattern.compile("([0-9]|)*")
    }

    /**
     * @param source    新输入的字符串
     * @param start     新输入的字符串起始下标，一般为0
     * @param end       新输入的字符串终点下标，一般为source长度-1
     * @param dest      输入之前文本框内容
     * @param dstart    原内容起始坐标，一般为0
     * @param dend      原内容终点坐标，一般为dest长度-1
     * @return          输入内容
     */
    override fun filter(source: CharSequence?, start: Int, end: Int,
                        dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        val sourceText = source?.toString()?:""
        val destText = dest?.toString()?:""

        //验证删除等按键
        if (sourceText.isEmpty()) {
            return ""
        }

        if (allowPoint){
            //允许输入小数点
            val matcher = mPattern.matcher(source?:"")
            //已经输入小数点的情况下，只能输入数字
            if (destText.contains(POINTER)) {
                if (!matcher.matches()) {
                    return ""
                } else if (POINTER == source.toString()) {
                    //只能输入一个小数点
                    return ""
                }
                //验证小数点精度，保证小数点后只能输入两位
                val index = destText.indexOf(POINTER)
                val length = dend - index

                if (length > POINTER_LENGTH) {
                    return dest?.subSequence(dstart, dend) ?:""
                }
            } else {
                /**
                 * 没有输入小数点的情况下，只能输入小数点和数字
                 * 1. 首位不能输入小数点
                 * 2. 如果首位输入0，则接下来只能输入小数点了
                 */
                if (!matcher.matches()) {
                    return ""
                } else {
                    if (POINTER == sourceText && TextUtils.isEmpty(destText)) {
                        //首位不能输入小数点
                        return ""
                    } else if (POINTER != sourceText && ZERO == destText) {
                        //如果首位输入0，接下来只能输入小数点
                        return ""
                    }
                }
            }
        }else{
            //不允许输入小数点
            val matcher = mNotPointPattern.matcher(source?:"")
            if (!matcher.matches()) {
                return ""
            } else {
                if (destText.isEmpty() && ZERO == sourceText) {
                    //首位不能输入0
                    return ""
                }
            }
        }



        //验证输入金额的大小
        val sumText ="$destText$sourceText".toDoubleOrNull()?:0.0
        return if (sumText > maxAmount) {
            dest?.subSequence(dstart, dend)?:""
        } else {
            "${dest?.subSequence(dstart, dend).toString()}$sourceText"
        }
    }

}