package com.example.basiccalculator

sealed class Operations(
    val symbol: String
){
    data object Add: Operations("+")
    data object Subtract: Operations("-")
    data object Multiply: Operations("x")
    data object Divide: Operations("/")
}