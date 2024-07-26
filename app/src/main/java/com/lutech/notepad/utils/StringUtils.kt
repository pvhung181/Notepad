package com.lutech.notepad.utils

fun String.countWord() : Int {
    val words = this.split(Regex("\\s+"))
    val nonEmptyWords = words.filter { it.isNotBlank() }
    return nonEmptyWords.size
}