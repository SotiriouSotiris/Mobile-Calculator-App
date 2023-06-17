package com.example.mobilecalculator

import android.database.sqlite.SQLiteDatabase.openDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.room.Room
import com.example.mobilecalculator.DatabaseProvider.getDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private lateinit var calculator: Calculator

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation

    private lateinit var database : AppDatabase

    private var usersCreated = 0

    fun onButtonClick(view: View) {
        val buttonTag = view.tag as? String
        if (!buttonTag.isNullOrEmpty()) {
            calculator.updateTextView(buttonTag)
        }
    }

    suspend fun fetchAllDataFromDatabase(){
        withContext(Dispatchers.IO){
            val dao = database.userDao()
            val users = dao.getAll()
            usersCreated = users.size
        }
    }

    suspend fun insertRecordInDatabase(name: String, last: String){
        withContext(Dispatchers.IO){
            val dao = database.userDao()

            val entity = User(usersCreated, name, last)
            dao.insertAll(entity)

            usersCreated+=1
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

        CoroutineScope(Dispatchers.Default).launch {
            val db = getDatabase(applicationContext)
            database = db

            fetchAllDataFromDatabase()

            insertRecordInDatabase("Sotiris", "Sotiriou")
        }

    }
}