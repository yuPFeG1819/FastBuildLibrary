package com.yupfeg.base.tools.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * 添加GlideApp支持，第一次添加需要重新reBuild项目
 * @author yuPFeG
 * @date 2019/9/9
 */

@GlideModule
class GlideModule : AppGlideModule(){
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
    }
}