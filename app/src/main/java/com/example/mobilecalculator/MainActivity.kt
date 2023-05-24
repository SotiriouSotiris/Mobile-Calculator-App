package com.example.mobilecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var calculator: Calculator

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation

    fun onButtonClick(view: View) {
        val buttonTag = view.tag as? String
        if (!buttonTag.isNullOrEmpty()) {
            calculator.updateTextView(buttonTag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewElement = findViewById<TextView>(R.id.tvResultView)

        val notValidElement = findViewById<TextView>(R.id.tvNotValidNotification)
        // Load the fade-in and fade-out animations
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        val screenWidthPixels = resources.displayMetrics.widthPixels

        // Set text size based on screen width
        val textSize = screenWidthPixels / 20   // Adjust the value as per your requirements

        // Convert pixels to scaled pixels (sp)
        val scaledTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            textSize.toFloat(),
            resources.displayMetrics
        )

        // Set the text size of the TextView
        textViewElement.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledTextSize)

        calculator = Calculator(mutableListOf<String>(), textViewElement, notValidElement, handler, fadeInAnimation, fadeOutAnimation, mutableListOf<String>())
        calculator.setValidButtonsToClick()

    }
}