package com.example.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    class MyView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) : View(context, attrs, defStyleAttr, defStyleRes) {

        private val paint = Paint()
        private var angle : Float = 0F
        private var dotsAngle : Float = 0F
        private var stage = 0
        private var bigDotRad : Float = 10F
        private var rotationShift : Float
        private var dotShift : Float
        private val valueColor : Int

        init {
            val a: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MyView, defStyleAttr, defStyleRes)
            try {
                valueColor = a.getColor(R.styleable.MyView_valueColor, WHITE)
                rotationShift = 360 / (a.getFloat(R.styleable.MyView_rotationDuration, 1000F) / 16)
                dotShift = 80 / (a.getFloat(R.styleable.MyView_dotDuration, 2000F) / 16)
            } finally {
                a.recycle()
            }
        }

        init {
            with(paint) {
                color = valueColor
                alpha = 255
                textSize = 500F
                isAntiAlias = true
                strokeWidth = 25F
            }
        }

        public override fun onSaveInstanceState(): Parcelable? {
            val savedState = SavedState(super.onSaveInstanceState())
            savedState.angle = angle
            savedState.dotsAngle = dotsAngle
            savedState.stage = stage
            savedState.bigDotRad = bigDotRad
            return savedState
        }

        public override fun onRestoreInstanceState(state: Parcelable) {
            if (state is SavedState) {
                super.onRestoreInstanceState(state.superState)
                angle = state.angle
                dotsAngle = state.dotsAngle
                stage = state.stage
                bigDotRad = state.bigDotRad
            } else {
                super.onRestoreInstanceState(state)
            }
        }

        internal class SavedState : BaseSavedState {
            var angle : Float = 0F
            var dotsAngle : Float = 0F
            var stage = 0
            var bigDotRad : Float = 10F

            constructor (source : Parcel) : super(source) {
                angle = source.readFloat()
                dotsAngle = source.readFloat()
                stage = source.readInt()
                bigDotRad = source.readFloat()
            }

            constructor(superState: Parcelable?) : super(superState)

            override fun writeToParcel(out: Parcel, flags: Int) {
                super.writeToParcel(out, flags)
                out.writeFloat(angle)
                out.writeFloat(dotsAngle)
                out.writeInt(stage)
                out.writeFloat(bigDotRad)
            }

            companion object CREATOR : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
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
                        angle = min(360F, angle + rotationShift)
                        rotate(angle, w / 4, h / 2)
                        if (kotlin.math.abs(angle) >= 360F) {
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
                        bigDotRad = min(20F, bigDotRad + dotShift)
                        if (bigDotRad >= 20) {
                            dotShift *= -1
                        } else if (bigDotRad <= 10F) {
                            dotsAngle += 90
                            dotShift *= -1
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