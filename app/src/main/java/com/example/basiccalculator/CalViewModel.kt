package com.example.basiccalculator

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Stack


fun evaluateExpression(expression: String): Double {
    val tokens = tokenize(expression)
    val postfix = infixToPostfix(tokens)
    return calculatePostfix(postfix)
}

fun tokenize(expression: String): List<String> {
    val regex = Regex("([0-9]+\\.?[0-9]*)|[+\\-x/*()]")
    return regex.findAll(expression.replace("x", "*")).map { it.value }.toList()
}

fun infixToPostfix(tokens: List<String>): List<String> {
    val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)
    val output = mutableListOf<String>()
    val operators = Stack<String>()

    for (token in tokens) {
        when {
            token.matches(Regex("[0-9]+\\.?[0-9]*")) -> output.add(token)
            token in "+-*/" -> {
                while (operators.isNotEmpty() && precedence[operators.peek()]!! >= precedence[token]!!) {
                    output.add(operators.pop())
                }
                operators.push(token)
            }
            token == "(" -> operators.push(token)
            token == ")" -> {
                while (operators.peek() != "(") {
                    output.add(operators.pop())
                }
                operators.pop()
            }
        }
    }
    while (operators.isNotEmpty()) {
        output.add(operators.pop())
    }
    return output
}
fun Char.isOperator(): Boolean {
    return this == '+' || this == '-' || this == 'x' || this == '/'
}


fun calculatePostfix(postfix: List<String>): Double {
    val stack = Stack<Double>()
    for (token in postfix) {
        when {
            token.matches(Regex("[0-9]+\\.?[0-9]*")) -> stack.push(token.toDouble())
            token in "+-*/" -> {
                val b = stack.pop()
                val a = stack.pop()
                stack.push(
                    when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        else -> throw IllegalArgumentException("Invalid operator")
                    }
                )
            }
        }
    }
    return stack.pop()
}



class CalViewModel : ViewModel() {
    var state by mutableStateOf(CalState())
        private set




    fun onAction(action: Actions) {
        when (action) {
            is Actions.Number -> enterNumber(action.number)
            is Actions.Delete -> delete()
            is Actions.Clear -> state = CalState()
            is Actions.Decimal -> enterDecimal()
            is Actions.Operation -> enterOperation(action.operation)
            is Actions.Calculate -> calculate()

        }
    }
    @SuppressLint("DefaultLocale")
    private fun evaluateExpression() {
        try {
            val evalResult = evaluateExpression(state.expression)
            if (evalResult % 1 == 0.0) {
                // If there is no decimal part, show the result without decimals
                state = state.copy(result = evalResult.toInt().toString())
            } else {
                // If there is a decimal part, format to 2 decimal places (or your desired limit)
                val formattedResult = String.format("%.8f", evalResult)
                state = state.copy(result = formattedResult)
            }
        } catch (e: Exception) {
            state = state.copy(result = "Error")
        }
    }

    private fun calculate() {
        state = state.copy(expression = state.result)
    }


    private fun enterOperation(operation: Operations) {
        if (state.expression.isNotEmpty() && !state.expression.last().isOperator()) {
            state = state.copy(expression = state.expression + operation.symbol)
        }
    }

    private fun enterDecimal() {
        state = state.copy(expression = state.expression + ".")


    }

    private fun delete() {
        if (state.expression.isNotEmpty()) {
            state = state.copy(expression = state.expression.dropLast(1))
            evaluateExpression()
        }
    }

    private fun enterNumber(number: Int) {
        state = state.copy(expression = state.expression + number)
        evaluateExpression()


    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}