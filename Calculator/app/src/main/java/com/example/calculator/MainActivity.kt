package com.example.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.parcel.Parcelize

class MainActivity : AppCompatActivity() {
    private var numberString : String = "0"
    private var dotHere : Boolean = false
    private var needToClean : Boolean = false
    private var inError : Boolean = false
    private var operationNumber : Double = 0.0

    @Parcelize
    enum class OperationTypes : Parcelable {
        NONE,
        PLUS,
        MINUS,
        MUL,
        DIV
    }

    private var operation = OperationTypes.NONE

    companion object {
        const val NUMBER_STRING = "NUMBER_STRING"
        const val DOT_HERE = "DOT_HERE"
        const val NEED_TO_CLEAN = "NEED_TO_CLEAN"
        const val IN_ERROR = "IN_ERROR"
        const val OPERATION_NUMBER = "OPERATION_NUMBER"
        const val OPERATION = "OPERATION"
    }

    private fun updateLable() {
        val fontSize = lable.textSize * 0.25
        val wigth = resources.configuration.screenWidthDp
        val labelSize = (wigth / fontSize).toInt()

        Log.i("WIDTH", wigth.toString())
        Log.i("FONT", (numberString.length * fontSize).toString())
        if (numberString == "Infinity") {
            inError = true
            numberString = getString(R.string.errorMessage)
        } else if (numberString.length >= labelSize) {
            inError = true
            numberString = numberString.substring(0, (wigth / fontSize).toInt()) + ".."
        }

        if (inError) {
            lable.setTextColor(getColor(R.color.colorError))
        } else {
            lable.setTextColor(getColor(R.color.colorBlack))
        }
        lable.text = numberString
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(NUMBER_STRING, numberString)
        outState.putBoolean(DOT_HERE, dotHere)
        outState.putBoolean(NEED_TO_CLEAN, needToClean)
        outState.putBoolean(IN_ERROR, inError)
        outState.putDouble(OPERATION_NUMBER, operationNumber)
        outState.putParcelable(OPERATION, operation)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        numberString = savedInstanceState.getString(NUMBER_STRING).toString()
        dotHere = savedInstanceState.getBoolean(DOT_HERE)
        needToClean = savedInstanceState.getBoolean(NEED_TO_CLEAN)
        inError = savedInstanceState.getBoolean(IN_ERROR)
        operationNumber = savedInstanceState.getDouble(OPERATION_NUMBER)
        operation = savedInstanceState.getParcelable(OPERATION)!!

        if (numberString != "0") {
            updateLable()
        }
    }

    private fun clickNumberButton(num: Int) {
        if (numberString == "0" || needToClean || inError) {
            numberString = num.toString()
            needToClean = false
            dotHere = false
            inError = false
        } else {
            numberString += num.toString()
        }
        updateLable()
    }

    private fun initNumberButtons() {
        zero.setOnClickListener{
            clickNumberButton(0)
        }
        one.setOnClickListener{
            clickNumberButton(1)
        }
        two.setOnClickListener{
            clickNumberButton(2)
        }
        three.setOnClickListener{
            clickNumberButton(3)
        }
        four.setOnClickListener{
            clickNumberButton(4)
        }
        five.setOnClickListener{
            clickNumberButton(5)
        }
        six.setOnClickListener{
            clickNumberButton(6)
        }
        seven.setOnClickListener{
            clickNumberButton(7)
        }
        eight.setOnClickListener {
            clickNumberButton(8)
        }
        nine.setOnClickListener{
            clickNumberButton(9)
        }
    }

    private fun normalizeNumberString() {
        if (numberString.length >= 3 &&
            numberString.substring(
                numberString.length - 2,
                numberString.length
            ) == ".0"
        ) {
            numberString = numberString.substring(0, numberString.length - 2)
        }
    }

    private fun solveOperation(operand: Double) {
        val result = when (operation) {
            OperationTypes.PLUS -> operationNumber + operand
            OperationTypes.MINUS -> operationNumber - operand
            OperationTypes.MUL -> operationNumber * operand
            OperationTypes.DIV -> operationNumber / operand
            else -> 0
        }

        numberString = result.toString()
        needToClean = true
        operationNumber = numberString.toDouble()
        normalizeNumberString()
        updateLable()
    }

    private fun initOneOperationButton(button: Button, newOperation: OperationTypes) {
        button.setOnClickListener {
            if (!inError) {
                if (operation == OperationTypes.NONE) {
                    operation = newOperation
                    needToClean = true
                    operationNumber = numberString.toDouble()
                } else {
                    if (needToClean) {
                        operation = newOperation
                    } else {
                        val secondOperand = numberString.toDouble()
                        solveOperation(secondOperand)
                        operation = newOperation
                        needToClean = true
                    }
                }
            }
        }
    }

    private fun initOperationButtons() {
        initOneOperationButton(plus, OperationTypes.PLUS)
        initOneOperationButton(minus, OperationTypes.MINUS)
        initOneOperationButton(mul, OperationTypes.MUL)
        initOneOperationButton(div, OperationTypes.DIV)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val myClipboard: ClipboardManager
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateLable()
        initNumberButtons()

        lable.setOnClickListener {
            val myClip : ClipData
            myClip = ClipData.newPlainText("label", lable.text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(applicationContext, getString(R.string.copyMessage), Toast.LENGTH_SHORT).show()
        }

        del.setOnClickListener {
            if (!inError) {
                if (numberString.length == 1) {
                    numberString = "0"
                } else {
                    if (numberString.get(numberString.length - 1) == '.') {
                        dotHere = false
                    }
                    numberString = numberString.substring(0, numberString.length - 1)
                }
                updateLable()
            }
        }

        dot.setOnClickListener {
            if (!inError) {
                if (needToClean) {
                    numberString = "0."
                    needToClean = false
                } else if (!dotHere) {
                    numberString += "."
                }
                dotHere = true
                updateLable()
            }
        }

        cancel.setOnClickListener {
            numberString = "0"
            dotHere = false
            operation = OperationTypes.NONE
            needToClean = false
            operationNumber = 0.0
            inError = false
            updateLable()
        }

        initOperationButtons()

        solve.setOnClickListener {
            if (!inError) {
                val secondOperand = numberString.toDouble()
                solveOperation(secondOperand)
                operation = OperationTypes.NONE
            }
        }

    }
}