package com.yupfeg.base.widget.ext

import android.annotation.TargetApi
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


// <editor-fold desc="软键盘">

/**
 * [EditText]拓展函数，设置软键盘高度监听
 * 内部利用setOnApplyWindowInsetsListener，如果外部调用同样函数，会覆盖该实现
 * @param listener 软键盘状态回调监听，imeVisible-是否显示，imeHeight-软键盘高度
 * */
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun EditText.setKeyBoardListener(listener : (imeVisible : Boolean, imeHeight : Int)->Unit){
    ViewCompat.setOnApplyWindowInsetsListener(this){ _, insets->
        val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        listener(imeVisible,imeHeight)
        insets
    }
}

/**
 * [EditText]拓展函数，显示软键盘
 * - 同时获取焦点
 * */
@Suppress("unused")
fun EditText.showKeyboard() {
    this.requestFocus()
    ViewCompat.getWindowInsetsController(this)?.apply{
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        show(WindowInsetsCompat.Type.ime())
    }
}

/**
 * [EditText]拓展函数，隐藏软键盘
 * - 同时清除焦点
 * */
@Suppress("unused")
fun EditText.hideKeyboard(){
    this.clearFocus()
    ViewCompat.getWindowInsetsController(this)?.apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.ime())
    }
}

/**
 * [EditText]的拓展函数，设置软键盘的搜索按钮点击事件
 * @param onSearch
 */
@Suppress("unused")
fun EditText.setKeyboardSearchClick(onSearch : ()->Unit){
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId != EditorInfo.IME_ACTION_SEARCH){
            onSearch.invoke()
            true
        }else{
            false
        }
    }
}

// </editor-fold>

// <editor-fold desc="文本输入监听">

/**
 * 创建监听文本输入结束内容变化的flow数据流
 * @param timeout 过滤输入文本时间，单位ms，避免输入过于频繁
 * @param filter 对输入文本进行过滤
 * */
@Suppress("unused")
@MainThread
@FlowPreview
fun EditText.createAfterTextChangeFlow(
    timeout : Long = 200,
    filter : (Editable)->Boolean = {true}
) : Flow<Editable>{
    return flow {
        emit(getAfterTextChangedSuspend())
    }
        .debounce(timeout)
        .filter { filter(it) }
}

/**
 * [EditText]的拓展函数，获取挂起的文本输入结束变化的内容，挂起函数，只能在协程使用
 * */
suspend fun EditText.getAfterTextChangedSuspend() : Editable{
    return suspendCancellableCoroutine {cont->
        val textWatcher = DSLTextWatcher().apply {
            afterTextChanged = {editable : Editable? ->
                editable?.also { cont.resume(it) }
            }
        }
        //协程结束时，移除输入监听
        cont.invokeOnCancellation {
            removeTextChangedListener(textWatcher)
        }
        addOnTextChangedWatcher(textWatcher)
    }
}

/**
 * [EditText]的拓展函数，使用Kotlin DSL方式，添加输入文本变化监听
 * @param textWatcher dsl方式，配置需要实现的文本变化监听
 * */
@Suppress("unused")
fun EditText.addOnTextChangedWatcher(textWatcher : DSLTextWatcher.()->Unit){
    this.addTextChangedListener(DSLTextWatcher().apply(textWatcher))
}

/**
 * [EditText]的拓展函数，添加输入变化监听
 * @param textWatcher 简化后的textWatcher，可选择实现
 * */
fun EditText.addOnTextChangedWatcher(textWatcher: DSLTextWatcher){
    this.addTextChangedListener(textWatcher)
}

/**
 * 简化在Kotlin下的EditText的TextWatch设置属性
 */
class DSLTextWatcher : TextWatcher{
    /**文本输入变化前执行动作*/
    @JvmField
    var beforeTextChanged : (
        (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit
    )? = null

    /**文本输入变化回调*/
    @JvmField
    var onTextChanged: (
        (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit
    )? = null

    /**文本输入结束后回调*/
    @JvmField
    var afterTextChanged : ((s: Editable?) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTextChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged?.invoke(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
        afterTextChanged?.invoke(s)
    }

}

// </editor-fold>