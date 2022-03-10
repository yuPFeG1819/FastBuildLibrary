package com.yupfeg.sample.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.tools.databinding.proxy.bindingDialog
import com.yupfeg.base.tools.ext.dipToPx
import com.yupfeg.drawable.rotateDrawable
import com.yupfeg.sample.R
import com.yupfeg.sample.databinding.DialogTestBindBinding

/**
 *
 * @author yuPFeG
 * @date
 */
class TestBindingDialog(
    context: Context, lifecycle: Lifecycle
) : AppCompatDialog(context, R.style.DialogStyle){

    private val mBinding : DialogTestBindBinding by bindingDialog(R.layout.dialog_test_bind,lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.loadingDrawable = createLoadingDrawable()
        window?.apply {
            val params = attributes
            params.gravity = Gravity.CENTER    //设置dialog位置
            attributes = params
            //设置背景透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(100.dipToPx(),ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun createLoadingDrawable() : Drawable{
        return rotateDrawable {
            drawable = ContextCompat.getDrawable(context,R.drawable.fast_build_recyclerview_footer_loading)
            fromDegrees = 0f
            toDegrees = 360f
        }
    }
}