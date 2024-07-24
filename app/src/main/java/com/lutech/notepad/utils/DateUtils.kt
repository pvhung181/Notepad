package com.lutech.notepad.utils

import java.text.SimpleDateFormat
import java.util.Date

fun formatDate(date: Date): String {
    return SimpleDateFormat("dd/M/yyyy hh:mm a").format(date)
}