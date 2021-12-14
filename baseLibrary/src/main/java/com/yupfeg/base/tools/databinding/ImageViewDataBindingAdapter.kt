package com.yupfeg.base.tools.databinding

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.yupfeg.base.tools.image.config.ImageRoundCornerType
import com.yupfeg.base.tools.image.ext.loadImage

/**
 * [ImageView]拓展函数，设置本地图片resId
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param resource 本地图片id
 */
@Suppress("unused")
@BindingAdapter("imageResource")
fun ImageView.bindSetImageViewResource(@DrawableRes resource : Int?){
    resource?:return
    if (resource == 0) return
    this.setImageResource(resource)
}

/**
 * [ImageView]拓展函数，加载图片
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 图片加载路径，默认为""，加载默认的占位图
 */
@Suppress("unused")
@BindingAdapter("imageUrl")
fun ImageView.bindLoadImageToView(imgPath: String? = ""){
    this.loadImage { this.imgPath = imgPath?:"" }
}

/**
 * [ImageView]拓展函数，加载图片
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 图片加载路径，默认为""，加载[placeholder]的占位图
 * @param placeholder 占位图Drawable,
 */
@Suppress("unused")
@BindingAdapter(value = ["imageUrl", "placeHolder"])
fun ImageView.bindLoadImageToView(imgPath : String? = "", placeholder : Drawable?){
    this.loadImage {
        this.imgPath = imgPath ?: ""
        this.placeholder = placeholder
    }
}

/**
 * [ImageView]拓展函数，加载图片
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 图片加载路径，默认为""，显示[placeholder]的占位图，加载失败则显示[error]的占位图
 * @param placeholder 占位图Drawable,
 * @param error 错误图drawable
 */
@Suppress("unused")
@BindingAdapter(value = ["imageUrl","placeHolder","imageError"])
fun ImageView.bindLoadImageToView(imgPath : String? = "", placeholder : Drawable?, error : Drawable?){
    this.loadImage{
        this.imgPath = imgPath ?: ""
        this.placeholder = placeholder
        this.error = error
    }
}

/**
 * [ImageView]拓展函数，加载图片
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 图片加载路径，默认为""，显示[placeholderId]的占位图
 * @param placeholderId 占位图resId
 */
@Suppress("unused")
@BindingAdapter(value = ["imageUrl","placeHolderId"])
fun ImageView.bindLoadImageToView(imgPath : String? = "",
                                  @DrawableRes placeholderId : Int?){
    this.loadImage{
        this.imgPath = imgPath ?: ""
        this.placeholderId = placeholderId?: Color.TRANSPARENT
    }
}

/**
 * [ImageView]拓展函数，加载图片
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 图片加载路径，默认为""，显示[placeholderId]的占位图，加载失败则显示[errorId]的占位图
 * @param placeholderId 占位图resId
 * @param errorId 错误图resId
 */
@Suppress("unused")
@BindingAdapter(
    value = ["imageUrl","placeHolderId","errorId"],
    requireAll = false
)
fun bindLoadImageToView(imageView : ImageView, imgPath : String? = "",
                        @DrawableRes placeholderId : Int?, @DrawableRes errorId : Int?){
    imageView.loadImage {
        this.imgPath = imgPath ?: ""
        this.placeholderId = placeholderId?: Color.TRANSPARENT
        this.errorId = errorId?: Color.TRANSPARENT
    }
}

/**
 * [ImageView]拓展函数，加载圆角图片url
 *
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 地图加载地址
 * @param placeholderId 图片加载默认占位图id、加载错误图片公用
 * @param radius 图片圆角弧度
 * @param roundCornerType 图片圆角类型
 */
@Suppress("unused")
@BindingAdapter(
    value = ["imageUrl","placeHolderId", "roundRadius","roundCornerType"]
)
fun ImageView.bindLoadRoundedImageToView(
    imgPath : String? = "", @DrawableRes placeholderId : Int,
    radius : Int, roundCornerType: ImageRoundCornerType
){
    this.loadImage {
        this.imgPath = imgPath?:""
        this.placeholderId = placeholderId
        this.radius = radius
        this.roundCornerType = roundCornerType
    }
}

/**
 * [ImageView]拓展函数，加载圆角图片url
 *
 * * DataBinding专用函数，[ImageView]作为DataBinding调用的主体，
 * 所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param imgPath 地图加载地址
 * @param placeholderId 图片加载默认占位图id、加载错误图片公用
 * @param radius 图片圆角弧度
 */
@Suppress("unused")
@BindingAdapter(
    value = ["imageUrl","placeHolderId", "roundRadius","errorId","roundCornerType"]
)
fun ImageView.bindLoadRoundedImageToView(
    imgPath : String? = "", @DrawableRes placeholderId : Int,@DrawableRes errorId: Int,
    radius : Int, roundCornerType: ImageRoundCornerType
){
    this.loadImage {
        this.imgPath = imgPath?:""
        this.placeholderId = placeholderId
        this.errorId = errorId
        this.radius = radius
        this.roundCornerType = roundCornerType
    }
}