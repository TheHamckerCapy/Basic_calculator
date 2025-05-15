package com.example.basiccalculator

sealed class Actions {
    data class Number(val number: Int) : Actions()
    data object Clear : Actions()
    data object Decimal : Actions()
    data object Delete : Actions()
    data object Calculate : Actions()
    data class Operation(val operation : Operations) : Actions()


}