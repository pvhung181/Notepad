package com.lutech.notepad.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText

fun applyBoldNoSelection(editText : EditText, start : Int, end : Int) {
    val spannable = editText.text as Spannable
    spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun applyItalicNoSelection(editText: EditText, start: Int, end: Int){
    val spannable = editText.text as Spannable
    spannable.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun applyUnderlineNoSelection(editText: EditText, start: Int, end: Int){
    val spannable = editText.text as Spannable
    spannable.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun applyStrikethroughNoSelection(editText: EditText, start: Int, end: Int){
    val ssb = editText.text as Spannable
    ssb.setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    editText.setSelection(end)
}
fun applyForegroundColorNoSelection(editText: EditText, start: Int, end: Int, color : Int){
    val spannable = editText.text as Spannable
    spannable.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun applyBackgroundColorNoSelection(editText: EditText, start: Int, end: Int, color : Int){
    val spannable = editText.text as Spannable
    spannable.setSpan(BackgroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun applyTextSizeNoSelection(editText: EditText, start: Int, end: Int, size: Int){
    val spannable = editText.text as Spannable
    spannable.setSpan(AbsoluteSizeSpan(size, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}