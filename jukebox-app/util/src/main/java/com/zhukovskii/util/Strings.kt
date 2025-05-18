package com.zhukovskii.util

fun String.justifyLeft(requiredLength: Int, fillSymbol: CharSequence): String {
    if (length >= requiredLength) return this

    return fillSymbol.repeat(requiredLength - length) + this
}