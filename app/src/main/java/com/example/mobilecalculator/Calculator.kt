package com.example.mobilecalculator

import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder

class Calculator(
    private var previousButtonClicked: MutableList<String>,
    private val textViewElement: TextView,
    private val notValidElement: TextView,
    private val handler: Handler,
    private val fadeInAnim: Animation,
    private val fadeOutAnim: Animation,
    private var validButtonsToClick: MutableList<String>,
    private var nextParenthesisOpen: String = "Open",
    private var dotInputtedAlready: Boolean = false
) {

    fun calculateEquation(equation: String): Double? {
        try {
            val expression = ExpressionBuilder(equation).build()
            return expression.evaluate()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun setValidButtonsToClick(){
        validButtonsToClick = getNextValidButtons()
    }

    fun getNextValidButtons(): MutableList<String> {
        // create a list(tempList) of valid buttons to click
        val lastButtonClicked = previousButtonClicked.lastOrNull()

        val tempList = mutableListOf<String>("C", "Undo")
        var isNumber = -1
        if(lastButtonClicked != null) {
            if(lastButtonClicked.toIntOrNull() != null){
                isNumber = lastButtonClicked.toInt()
            }
        }
        val isMathSymb = lastButtonClicked in listOf<String>("%", "/", "x", "-", "+")

        if (lastButtonClicked == ".") {
            for (i in 0..9) {
                tempList.add(i.toString())
            }

        } else if (isMathSymb) {
            for (i in 0..9) {
                tempList.add(i.toString())
            }
            tempList.add("()")

        } else if (lastButtonClicked == "=") {
            tempList.addAll(listOf("%", "/", "x", "-", "+"))
            for (i in 0..9) {
                tempList.add(i.toString())
            }

        } else if (lastButtonClicked == "C" || lastButtonClicked == null) {
            tempList.removeAll(listOf("C", "Undo"))
            for (i in 0..9) {
                tempList.add(i.toString())
            }
            tempList.addAll(listOf("()", "-"))

        } else if (isNumber != -1 && isNumber in 0..9) {
            tempList.addAll(listOf("%", "/", "x", "-", "+", "=", "()"))
            if (!dotInputtedAlready){
                tempList.add(".")
            }
            for (i in 0..9) {
                tempList.add(i.toString())
            }

        } else if (lastButtonClicked == "()") {
            // if next is open then the parenthesis just inputted is closed
            if (nextParenthesisOpen == "Closed") {
                tempList.addAll(listOf("%", "/", "x", "-", "+", "="))
                nextParenthesisOpen = "Open"
            } else {
                nextParenthesisOpen = "Closed"
                for (i in 0..9) {
                    tempList.add(i.toString())
                }
                tempList.add("-")
            }
        }

        return tempList
    }

    fun updateTextView(newInput: String){

        setValidButtonsToClick()

        // Undo was pressed and this is not the first action
        if (newInput == "Undo" && previousButtonClicked.removeLastOrNull() != null){
            // go back one action and reset the valid buttons to click
            textViewElement.text = textViewElement.text.dropLast(1)
            setValidButtonsToClick()
        }

        if(newInput in validButtonsToClick){

            if (newInput == "C"){
                textViewElement.text = ""
                previousButtonClicked = mutableListOf<String>()

            } else if (newInput == "=") {
                // calculate the equation
                var result = calculateEquation(textViewElement.text.toString())
                if(result != null) {
                    // check if result has decimal to show them or not
                    if (result.rem(1) == 0.0) {
                        textViewElement.text = result.toInt().toString()
                    } else {
                        textViewElement.text = result.toString()
                        dotInputtedAlready = true
                    }
                }

            } else if (newInput == "()"){
                if (nextParenthesisOpen == "Open"){
                    textViewElement.text = textViewElement.text.toString() + "("
                } else{
                    textViewElement.text = textViewElement.text.toString() + ")"
                }

            } else if(newInput != "Undo"){
                textViewElement.text = textViewElement.text.toString() + newInput
            }

            // update the previousButtonClicked list
            if (newInput != "Undo" && newInput != "=" && newInput != "C") {
                previousButtonClicked.add(newInput)
            }

        }else{
            // show not valid notification with animation
            notValidElement.startAnimation(fadeInAnim)
            notValidElement.visibility = View.VISIBLE

            handler.postDelayed({
                notValidElement.startAnimation(fadeOutAnim)
                notValidElement.visibility = View.INVISIBLE
            }, 1000L)
        }
    }

}