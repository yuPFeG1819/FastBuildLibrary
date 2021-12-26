package com.yupfeg.base.tools.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yupfeg.base.tools.image.opts.ImageLoadOptions
import com.yupfeg.base.tools.image.opts.ImageRoundCornerType
import com.yupfeg.base.tools.image.opts.ImageScaleType

/**
 * [Glide]的图片加载帮助类
 * @author yuPFeG
 * @date 2021/03/22
 */
class GlideLoadHelper : ImageLoadable {

    /**
     * 加载图片url
     * @param imageView
     * @param config 图片加载配置
     * */
    override fun loadImageToView(imageView: ImageView, config: ImageLoadOptions) {
        config.takeIf { it.localDrawableId != 0 }?.also {
            imageView.preformLoadPicture(it.localDrawableId,config)
        }?: config.imgUri ?.also {uri->
            imageView.preformLoadPicture(uri,config)
        }?: config.imgPath.also {imgPath->
            imageView.preformLoadPicture(imgPath,config)
        }
    }

    /**
     * 加载bitmap
     * @param config 图片加载配置
     * @param bitmapReadyAction 加载完成bitmap的执行动作
     * @param loadStartAction 初始加载的显示回调
     * @param loadErrorAction 加载失败的显示回调
     * */
    override fun loadBitmap(
        context: Context,
        config: ImageLoadOptions,
        bitmapReadyAction: (Bitmap) -> Unit,
        loadStartAction : ((Drawable?)->Unit)?,
        loadErrorAction : ((Drawable?)->Unit)?
    ) {
        config.takeIf { it.localDrawableId != 0 }.also {
            preformLoadBitmap(
                context,config,bitmapReadyAction,
                loadStartAction,loadErrorAction
            )
        }?: also {
            preformLoadBitmap(
                context,config,bitmapReadyAction,
                loadStartAction,loadErrorAction
            )
        }
    }

    /**
     * 清理指定View的bitmap缓存
     * @param imageView
     * */
    override fun cleanTargetViewBitmapCache(imageView: ImageView) {
        Glide.with(imageView.context).clear(imageView)
    }

    /**
     * 清理所有图片缓存
     * */
    override fun cleanAllCache(context: Context) {
        Glide.get(context).clearMemory()
        Glide.get(context).clearDiskCache()
    }

    /**
     * 执行加载图片
     * @param imgUrl 图片加载地址
     * @param config 图片加载的配置
     * */
    @SuppressLint("CheckResult")
    private fun ImageView.preformLoadPicture(imgUrl : Any, config: ImageLoadOptions){
        Glide.with(context).load(imgUrl).apply {
            setPlaceholder(config.placeholder,config.placeholderId)
            setErrorPlaceholder(config.error,config.errorId)
            //bitmap转换
            setImageBitmapTransform(config)
            //渐变动画
            transition(DrawableTransitionOptions.withCrossFade())
            //是否跳过内存缓存
            skipMemoryCache(config.isSkipMemoryCache)
            diskCacheStrategy(
                if (config.isSkipDiskCache) DiskCacheStrategy.NONE
                else DiskCacheStrategy.AUTOMATIC
            )
            //图片加载尺寸
            setOverrideSize(config.overrideWidth,config.overrideHeight)
            //优先加载缩略图
            thumbnail(config.thumbnail)
        }.into(this)
    }

    /**
     * 执行加载Bitmap
     * @param context
     * @param config
     * @param bitmapReadyAction bitmap加载准备执行动作，外部赋值到View
     * @param loadStartAction 初始加载的显示回调
     * @param loadErrorAction 加载失败的显示回调
     * */
    @SuppressLint("CheckResult")
    private fun preformLoadBitmap(
        context : Context, config: ImageLoadOptions,
        bitmapReadyAction: (Bitmap) -> Unit,
        loadStartAction : ((Drawable?)->Unit)?,
        loadErrorAction : ((Drawable?)->Unit)?
    ){
        val builder = createBitmapBuilderFromUrl(context,config.imgPath)
            ?:createBitmapBuilder(context,config.localDrawableId)
        builder?.apply {
            setPlaceholder(config.placeholder, config.placeholderId)
            setErrorPlaceholder(config.error, config.errorId)
            setImageBitmapTransform(config)
            //渐变动画
            transition(BitmapTransitionOptions.withCrossFade())
            //是否跳过内存缓存
            skipMemoryCache(config.isSkipMemoryCache)
            diskCacheStrategy(
                if (config.isSkipDiskCache) DiskCacheStrategy.NONE
                else DiskCacheStrategy.AUTOMATIC
            )
            //图片加载尺寸
            setOverrideSize(config.overrideWidth,config.overrideHeight)
            //优先加载缩略图
            thumbnail(config.thumbnail)
        }?.into(object : CustomTarget<Bitmap>(){
            override fun onLoadStarted(placeholder: Drawable?) {
                super.onLoadStarted(placeholder)
                loadStartAction?.invoke(placeholder)
            }
            override fun onLoadCleared(placeholder: Drawable?) {}
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapReadyAction.invoke(resource)
            }
            override fun onLoadFailed(errorDrawable: Drawable?) {
                loadErrorAction?.invoke(errorDrawable)
            }
        })
    }

    /**
     * 设置图片请求的占位图
     * @param placeholder 占位图[Drawable]
     * @param drawableRes 占位图的resId
     * */
    @SuppressLint("CheckResult")
    private fun RequestBuilder<*>.setPlaceholder(placeholder : Drawable?,
                                                 @DrawableRes drawableRes: Int){
        placeholder?.also{
            this.placeholder(it)
        }?: drawableRes.takeIf { it != 0 }?.also { resId->
            this.placeholder(resId)
        }
    }

    /**
     * 设置图片加载错误占位图
     * @param error 占位图[Drawable]
     * @param drawableRes 占位图的resId
     * */
    @SuppressLint("CheckResult")
    private fun RequestBuilder<*>.setErrorPlaceholder(error : Drawable?,
                                                      @DrawableRes drawableRes: Int){
        error?.also{
            this.error(it)
        }?: drawableRes.takeIf { it != 0 }?.also { resId->
            this.error(resId)
        }
    }

    /**
     * 设置图片Bitmap的转换操作
     * @param config
     */
    @SuppressLint("CheckResult")
    private fun RequestBuilder<*>.setImageBitmapTransform(config: ImageLoadOptions){

        if (config.roundCornerType == ImageRoundCornerType.CIRCLE){
            //图片剪裁类型
            val scaleTransformation = getScaleTypeTransform(config.scaleType)
            transform(scaleTransformation,CircleCrop())
        }else{
            //圆角加载类型
            val roundCornerType = obtainRoundCornerType(config.roundCornerType)
            roundCornerType?.also {
                //圆角变换
                val roundCornerTransform = getImageRoundCornerTransform(
                    config.radius,roundCornerType
                )
                //图片剪裁类型
                val scaleTransformation = getScaleTypeTransform(config.scaleType)
                transform(scaleTransformation,roundCornerTransform)
            }
        }
    }

    /**
     * [RequestBuilder]拓展函数，设置图片加载尺寸（覆盖原有默认配置）
     * @param width 图片宽度
     * @param height 图片高度
     * */
    @Suppress("unused")
    @SuppressLint("CheckResult")
    private fun RequestBuilder<*>.setOverrideSize(width : Int,height : Int){
        if (width == ImageLoadOptions.SIZE_ORIGINAL
            && height == ImageLoadOptions.SIZE_ORIGINAL){
            //加载到原图尺寸
            override(Target.SIZE_ORIGINAL)
        }else if (width == ImageLoadOptions.SIZE_FROM_TARGET_VIEW
            && height == ImageLoadOptions.SIZE_FROM_TARGET_VIEW){
            //采用默认配置，加载根据视图尺寸
            return
        }
        override(width,height)
    }

    /**
     * 获取图片剪裁类型的图片变换操作
     * * 避免外部ImageView设置scaleType为centerCrop后，无法设置圆角，需要与圆角转换一同设置
     * */
    private fun getScaleTypeTransform(
        scaleType: ImageScaleType
    ) : BitmapTransformation {
        return when(scaleType){
            ImageScaleType.CENTER_CROP -> CenterCrop()
            ImageScaleType.CENTER_INSIDE -> CenterInside()
            ImageScaleType.FIT_CENTER -> FitCenter()
        }
    }

    /**
     * 获取圆角图片的处理变换操作类
     * * 避免外部ImageView设置scaleType为centerCrop后，无法设置圆角
     * @param radius
     * @param roundCornerType
     * @return
     */
    private fun getImageRoundCornerTransform(
        radius : Int, roundCornerType: RoundedCornersTransformation.CornerType
    ) : BitmapTransformation{
        //添加圆角转换
        return RoundedCornersTransformation(radius, 0, roundCornerType)
    }

    /**尝试获取对应的圆角图片类型*/
    private fun obtainRoundCornerType(roundCornerType: ImageRoundCornerType) =
        when(roundCornerType){
            ImageRoundCornerType.ALL -> RoundedCornersTransformation.CornerType.ALL
            ImageRoundCornerType.TOP -> RoundedCornersTransformation.CornerType.TOP
            ImageRoundCornerType.BOTTOM -> RoundedCornersTransformation.CornerType.BOTTOM
            ImageRoundCornerType.LEFT -> RoundedCornersTransformation.CornerType.LEFT
            ImageRoundCornerType.RIGHT -> RoundedCornersTransformation.CornerType.RIGHT
            ImageRoundCornerType.TOP_LEFT -> RoundedCornersTransformation.CornerType.TOP_LEFT
            ImageRoundCornerType.TOP_RIGHT -> RoundedCornersTransformation.CornerType.TOP_RIGHT
            ImageRoundCornerType.BOTTOM_LEFT -> RoundedCornersTransformation.CornerType.BOTTOM_LEFT
            ImageRoundCornerType.BOTTOM_RIGHT -> RoundedCornersTransformation.CornerType.BOTTOM_RIGHT
            else -> null
        }


    /**创建图片url的图片请求Builder*/
    private fun createBitmapBuilderFromUrl(context : Context,
                                           imageUrl : String?) : RequestBuilder<Bitmap>?{
        imageUrl ?: return null
        return Glide.with(context).asBitmap().load(imageUrl)
    }

    /**创建本地Drawable的图片请求Builder*/
    private fun createBitmapBuilder(context : Context,
                                    @DrawableRes drawableRes : Int) : RequestBuilder<Bitmap>?{
        takeIf { drawableRes != 0 } ?: return null
        return Glide.with(context).asBitmap().load(drawableRes)
    }
}