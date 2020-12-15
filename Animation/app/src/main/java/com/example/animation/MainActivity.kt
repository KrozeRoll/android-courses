package com.example.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    class MyView : View {
//        //View(context, attrs, defStyleAttr = 0, defStyleRes = 0)
//        constructor(context: Context) : super(context)
//        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
//        constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int = 0)
//                : super(context, attrs, defStyleAttr)
//        constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int = 0, defStyleRes : Int = 0)
//                : super(context, attrs, defStyleAttr, defStyleRes)
//    }

    class MyView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private val paint = Paint()
        private var angle : Float = 0F
        private var dotsAngle : Float = 0F
        private var stage = 0
        private var bigDotRad : Float = 10F
        private var radiusShift : Float = 1F

        init {
            with(paint) {
                color = Color.WHITE
                alpha = 255
                textSize = 500F
                isAntiAlias = true
                strokeWidth = 25F
            }
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            if (canvas != null) {
                val w : Float = width.toFloat()
                val h : Float = height.toFloat()
                with(canvas) {
                    save()
                    if (stage == 0) {
                        drawCircle(3 * w / 4, h / 4, 10F, paint)
                        drawCircle(5 * w / 8, h / 2, 10F, paint)
                        drawCircle(3 * w / 4, 3 * h / 4, 10F, paint)
                        drawCircle(7 * w / 8, h / 2, 10F, paint)
                        clipRect(0F, 0F, w / 2, h)
                        angle += 7
                        rotate(angle, w / 4, h / 2)
                        if (angle >= 360F) {
                            angle = 0F
                            stage = (stage + 1) % 2
                        }
                        drawLine(w / 4, 0F, w / 4, h, paint)
                        drawLine(0F, h / 2, w / 2, h / 2, paint)
                    } else if (stage == 1) {
                        drawLine(w / 4, 0F, w / 4, h, paint)
                        drawLine(0F, h / 2, w / 2, h / 2, paint)
                        clipRect(w / 2, 0F, w, h)
                        rotate(dotsAngle, 3 * w / 4, h / 2)

                        drawCircle(3 * w / 4, h / 4, bigDotRad, paint)
                        drawCircle(5 * w / 8, h / 2, 10F, paint)
                        drawCircle(3 * w / 4, 3 * h / 4, 10F, paint)
                        drawCircle(7 * w / 8, h / 2, 10F, paint)
                        bigDotRad += radiusShift
                        if (bigDotRad >= 20) {
                            radiusShift = -1F
                        } else if (bigDotRad == 10F) {
                            dotsAngle += 90
                            radiusShift = 1F
                        }
                        if (dotsAngle == 360F) {
                            dotsAngle = 0F
                            stage = (stage + 1) % 2
                        }
                    }


                    restore()
                }
            }
            invalidate()
        }
    }
    //translate влево на пол вьюхи
    //clip квардратика
    //rotate
    //restore
    //translate right
    //отрисовка кружочков

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alphaAnimation =  ObjectAnimator.ofFloat(message, "alpha", 1f, 0.5f, 1f)
        with(alphaAnimation) {
            duration = 1000
            repeatCount = Animation.INFINITE
        }

        val scaleAnimation = ObjectAnimator.ofFloat(message, "scaleX", 1f, 1.3f, 1f)
        with(scaleAnimation) {
            duration = 1000
            repeatCount = Animation.INFINITE
        }


        val set = AnimatorSet()
        set.playTogether(alphaAnimation, scaleAnimation)
        set.start()
    }
}