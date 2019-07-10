package com.example.myapplication.day3

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.R
import com.pes.androidmaterialcolorpickerdialog.ColorPicker

class AnimActivity : AppCompatActivity() {

    private lateinit var target: View
    private lateinit var startColorPicker: View
    private lateinit var endColorPicker: View
    private lateinit var durationSelector: Button
    private lateinit var animatorSet: AnimatorSet

    private lateinit var animationView: LottieAnimationView
    private lateinit var loopCheckBox: CheckBox
    private lateinit var seekBar: SeekBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim)

        target = findViewById(R.id.target)
        startColorPicker = findViewById(R.id.start_color_picker)
        endColorPicker = findViewById(R.id.end_color_picker)
        durationSelector = findViewById(R.id.duration_selector)
        animatorSet = AnimatorSet()

        animationView = findViewById(R.id.animation_view)
        loopCheckBox = findViewById(R.id.loop_checkbox)
        seekBar = findViewById(R.id.seekbar)

        startColorPicker.setOnClickListener {
            // use of qualified this == AnimActivity.this in Java
            val picker = ColorPicker(this@AnimActivity)
            picker.color = getBackgroundColor(endColorPicker)
            picker.enableAutoClose()
            picker.setCallback { onStartColorChanged(it) }
            picker.show()
        }

        endColorPicker.setOnClickListener {
            val picker = ColorPicker(this@AnimActivity)
            picker.color = getBackgroundColor(endColorPicker)
            picker.enableAutoClose()
            picker.setCallback { color -> onEndColorChanged(color) }
            picker.show()
        }

        durationSelector.text = 1000.toString()
        durationSelector.setOnClickListener {
            MaterialDialog.Builder(this@AnimActivity)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(
                    getString(R.string.duration_hint),
                    durationSelector.text
                ) { _, input ->
                    onDurationChanged(input.toString())
                }
                .show()
        }
        resetTargetAnimation()

        loopCheckBox.setOnCheckedChangeListener { _, isChecked ->
            seekBar.isEnabled = !isChecked
            if (isChecked) {
                animationView.playAnimation()
                animationView.progress = seekBar.progress / 100.0f
            } else {
                animationView.pauseAnimation()
                val progress = (100 * animationView.progress).toInt()
                ValueAnimator.ofInt(0, progress).apply {
                    duration = 400
                    addUpdateListener { seekBar.progress = it.animatedValue.toString().toInt() }
                    repeatCount = 0
                    start()
                }
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // TODO ex1-2: 这里应该调用哪个函数呢
                if (fromUser) {
                    animationView.progress = progress / 100.0f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun onStartColorChanged(color: Int) {
        startColorPicker.setBackgroundColor(color)
        resetTargetAnimation()
    }

    private fun onEndColorChanged(color: Int) {
        endColorPicker.setBackgroundColor(color)
        resetTargetAnimation()
    }

    private fun onDurationChanged(input: String) {
        var isValid = true
        try {
            val duration = Integer.parseInt(input)
            if (duration < 100 || duration > 10000) {
                isValid = false
            }
        } catch (e: Throwable) {
            isValid = false
        }

        if (isValid) {
            durationSelector.text = input
            resetTargetAnimation()
        } else {
            Toast.makeText(this@AnimActivity, R.string.invalid_duration, Toast.LENGTH_LONG).show()
        }
    }

    private fun getBackgroundColor(view: View): Int {
        val bg = view.background
        return (bg as? ColorDrawable)?.color ?: Color.WHITE
    }

    private fun resetTargetAnimation() {
        animatorSet.cancel()

        // 在这里实现了一个 ObjectAnimator，对 target 控件的背景色进行修改
        // 可以思考下，这里为什么要使用 ofArgb，而不是 ofInt 呢？
        /* TODO：原因是以ARGB格式储存的颜色的变化与int的递增的变化不同，使用int的话
                animator使颜色的数值递增但是视觉上与我们所想颜色均匀变化的变化不同。
         */
        val animator1 = ObjectAnimator.ofArgb(
            target,
            "backgroundColor",
            getBackgroundColor(startColorPicker),
            getBackgroundColor(endColorPicker)
        )
        animator1.duration = Integer.parseInt(durationSelector.text.toString()).toLong()
        animator1.repeatCount = ObjectAnimator.INFINITE
        animator1.repeatMode = ObjectAnimator.REVERSE

        // TODO ex2-1：在这里实现另一个 ObjectAnimator，对 target 控件的大小进行缩放，从 1 到 2 循环
        val phx = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 2.0f)
        val phy = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 2.0f)
        val animator2 = ObjectAnimator.ofPropertyValuesHolder(
            target,
            phx, phy
        ).apply {
            duration = Integer.parseInt(durationSelector.text.toString()).toLong()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        // TODO ex2-2：在这里实现另一个 ObjectAnimator，对 target 控件的透明度进行修改，从 1 到 0.5f 循环
        val animator3 = ObjectAnimator.ofFloat(
            target,
            "alpha",
            1.0f, 0.5f
        ).apply {
            duration = Integer.parseInt(durationSelector.text.toString()).toLong()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        // TODO ex2-3: 将上面创建的其他 ObjectAnimator 都添加到 AnimatorSet 中
        animatorSet.playTogether(animator1, animator2, animator3)
        animatorSet.start()
    }



}

fun Context.animIntent(): Intent {
    return Intent(this, AnimActivity::class.java)
}