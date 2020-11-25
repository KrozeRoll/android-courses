package com.example.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

var numberString : String = "0";
var dotHere : Boolean = false;
var needToClean : Boolean = false;
var inError : Boolean = false;
var operationNumber : Double = 0.0;
var currentNumber : Double = 0.0;

enum class OperationTypes {
    NONE,
    PLUS,
    MINUS,
    MUL,
    DIV
}

var operation = OperationTypes.NONE;

class MainActivity : AppCompatActivity() {
    private fun updateLable() {
        if (numberString == "Infinity") {
            inError = true;
            numberString = getString(R.string.errorMessage);
        } else if (lable.width != 0 && numberString.length * 135 >= lable.width) {
            inError = true;
            numberString = numberString.substring(0, lable.width / 150) + "..";
        }

        if (inError) {
            lable.setTextColor(getColor(R.color.colorError))
        } else {
            lable.setTextColor(getColor(R.color.colorBlack))
        }
        lable.text = numberString
    }

    private fun clickNumberButton(num: Int) {
        if (numberString == "0" || needToClean || inError) {
            numberString = num.toString();
            needToClean = false;
            dotHere = false;
            inError = false;
        } else {
            numberString += num.toString();
        }
        updateLable();
    }

    private fun initNumberButtons() {
        zero.setOnClickListener{
            clickNumberButton(0);
        }
        one.setOnClickListener{
            clickNumberButton(1);
        }
        two.setOnClickListener{
            clickNumberButton(2);
        }
        three.setOnClickListener{
            clickNumberButton(3);
        }
        four.setOnClickListener{
            clickNumberButton(4);
        }
        five.setOnClickListener{
            clickNumberButton(5);
        }
        six.setOnClickListener{
            clickNumberButton(6);
        }
        seven.setOnClickListener{
            clickNumberButton(7);
        }
        eight.setOnClickListener {
            clickNumberButton(8);
        }
        nine.setOnClickListener{
            clickNumberButton(9);
        }
    }

    private fun normalizeNumberString() {
        if (numberString.length >= 3 &&
            numberString.substring(
                numberString.length - 2,
                numberString.length
            ) == ".0"
        ) {
            numberString = numberString.substring(0, numberString.length - 2);
        }
    }

    private fun solveOperation(operand: Double) {
        val result = when (operation) {
            OperationTypes.PLUS -> operationNumber + operand
            OperationTypes.MINUS -> operationNumber - operand
            OperationTypes.MUL -> operationNumber * operand
            OperationTypes.DIV -> operationNumber / operand
            else -> 0;
        }

        numberString = result.toString();
        needToClean = true;
        operationNumber = numberString.toDouble()
        normalizeNumberString();
        updateLable();
    }

    private fun initOneOperationButton(button: Button, newOperation: OperationTypes) {
        button.setOnClickListener {
            if (!inError) {
                if (operation == OperationTypes.NONE) {
                    operation = newOperation;
                    needToClean = true;
                    operationNumber = numberString.toDouble();
                } else {
                    if (needToClean) {
                        operation = newOperation;
                    } else {
                        val secondOperand = numberString.toDouble();
                        solveOperation(secondOperand);
                        operation = newOperation;
                        needToClean = true;
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
        updateLable();
        initNumberButtons();

        lable.setOnClickListener {
            val myClip : ClipData;
            myClip = ClipData.newPlainText("label", lable.text);
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(applicationContext, "Result copied!", Toast.LENGTH_SHORT).show()
        }

        del.setOnClickListener {
            if (!inError) {
                if (numberString.length == 1) {
                    numberString = "0";
                } else {
                    if (numberString.get(numberString.length - 1) == '.') {
                        dotHere = false;
                    }
                    numberString = numberString.substring(0, numberString.length - 1)
                }
                updateLable();
            }
        }

        dot.setOnClickListener {
            if (!inError) {
                if (needToClean) {
                    numberString = "0."
                    needToClean = false;
                } else if (!dotHere) {
                    numberString += ".";
                }
                dotHere = true;
                updateLable();
            }
        }

        cancel.setOnClickListener {
            numberString = "0";
            dotHere = false;
            operation = OperationTypes.NONE;
            needToClean = false;
            operationNumber = 0.0;
            inError = false;
            updateLable();
        }

        initOperationButtons();

        solve.setOnClickListener {
            if (!inError) {
                val secondOperand = numberString.toDouble();
                solveOperation(secondOperand);
                operation = OperationTypes.NONE;
            }
        }

    }
}