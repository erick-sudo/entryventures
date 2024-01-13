package com.entryventures.extensions

fun String.charactersUpto(stop: Char): String {
    val firstOccurrence = this.indexOf(stop)
    return this.substring(0..<firstOccurrence)
}