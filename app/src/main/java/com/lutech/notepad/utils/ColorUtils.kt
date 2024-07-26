package com.lutech.notepad.utils

import android.graphics.Color


fun darkenColor(hexColor: String): String {
    val factor = 0.8f
    // Chuyển đổi mã màu hex sang int
    val color = Color.parseColor(hexColor)

    // Lấy các thành phần RGB
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)

    // Giảm độ sáng của các thành phần RGB theo factor
    val darkerR = (r * factor).toInt().coerceAtLeast(0)
    val darkerG = (g * factor).toInt().coerceAtLeast(0)
    val darkerB = (b * factor).toInt().coerceAtLeast(0)

    // Tạo màu mới từ các thành phần RGB đã giảm
    val darkerColor = Color.rgb(darkerR, darkerG, darkerB)

    // Chuyển đổi màu mới thành mã màu hex
    return String.format("#%06X", (0xFFFFFF and darkerColor))
}