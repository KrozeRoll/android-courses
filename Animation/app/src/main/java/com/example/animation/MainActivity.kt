package com.example.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import androidx.core.animation.doOnRepeat
import kotlinx.android.synthetic.main.activity_main.*

import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class MainActivity : AppCompatActivity() {
    class MyView(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs) {
        val valueText : String
        init {
            val a: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MyView, defStyleAttr, defStyleRes)
            try {
                valueText = a.getString(R.styleable.MyView_valueText) ?: ""
            } finally {
                a.recycle()
            }
        }




    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alphaAnimation =  ObjectAnimator.ofFloat(message, "alpha", 1f, 0.5f, 1f);
        with(alphaAnimation) {
            setDuration(1000);
            setRepeatCount(Animation.INFINITE)
        }

        val scaleAnimation = ObjectAnimator.ofFloat(message, "scaleX", 1f, 1.3f, 1f )
        with(scaleAnimation) {
            setDuration(1000);
            setRepeatCount(Animation.INFINITE)
        }


        val set = AnimatorSet();
        set.playTogether(alphaAnimation, scaleAnimation);
        set.start()
    }
}