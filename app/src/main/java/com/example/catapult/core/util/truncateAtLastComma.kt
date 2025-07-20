package com.example.catapult.core.util

fun truncateAtLastComma(input: String, maxLength: Int): String {
    if (input.length <= maxLength) return input

    val truncated = input.take(maxLength)
    val lastCommaIndex = truncated.lastIndexOf('.')

    return if (lastCommaIndex != -1) {
        truncated.substring(0, lastCommaIndex + 1)
    } else {
        truncated
    }
}