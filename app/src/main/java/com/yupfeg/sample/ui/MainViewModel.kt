package com.yupfeg.sample.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.yupfeg.base.application.BaseApplication
import com.yupfeg.base.viewmodel.BaseViewModel
import com.yupfeg.drawable.*
import com.yupfeg.sample.domain.OtherUseCase
import com.yupfeg.sample.domain.TestUseCase

/**
 *
 * @author yuPFeG
 * @date
 */
class MainViewModel : BaseViewModel(){

    val testUseCase = TestUseCase()
    val otherUserCase = OtherUseCase()

    val testDrawableBg = createTestCodeDrawable()

    val testTextColorState = createTextColorStateList()

    init {
        addUseCase(testUseCase)
        addUseCase(otherUserCase)
    }

    private fun createTestCodeDrawable() : Drawable {
        return layerDrawable {
            //底层阴影drawable
            addItem {
                left = 3
                top = 3
                drawable = shapeDrawable {
                    solid = ContextCompat.getColor(BaseApplication.appContext,android.R.color.darker_gray)
                    radius = 12f
                }
            }
            //实际背景drawable
            addItem {
                right = 3
                bottom = 3
                drawable = shapeDrawable {
                    solid = selectorColor {
                        pressed = Color.GRAY //按下时显示颜色
                        normal = Color.YELLOW //默认状态
                    }
                    radius = 12f
                    stroke = shapeStroke {
                        color = Color.RED
                        width = 1f
                        dashWidth = 3f
                        dashGap = 2f
                    }
                }
            }
        }
    }

    private fun createTextColorStateList() : ColorStateList{
        return selectorColor {
            addItem{
                color = Color.WHITE
                addState(AttrStates.StatePressed,true)
            }
            addItem {
                color = Color.BLACK
                addState(AttrStates.StatePressed,false)
            }
        }
    }

}