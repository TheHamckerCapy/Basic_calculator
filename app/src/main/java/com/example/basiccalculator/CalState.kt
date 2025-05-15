package com.example.basiccalculator

data class CalState(
    val number1: String = "",
    val number2: String = "",
    val operation: Operations? = null,
    val expression: String = "",
    val result: String = ""
)
