package com.yupfeg.base.tools.ext

/**
 * 字符串的拓展方法提取
 * @author yuPFeG
 * @date 2020/05/23
 */

/**[String]拓展函数，获取保护隐私的手机号*/
@Suppress("unused")
fun String.getPrivacyPhone() : String{
    return if (isNotEmpty() && length == 11){
        "${substring(0,3)}****${substring(lastIndex-3,length)}"
    }else{
        this
    }
}