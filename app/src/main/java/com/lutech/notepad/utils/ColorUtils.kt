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

fun getColorWithOpacity(hexColor: String, opacity: Float): String {
    // Kiểm tra xem mã màu hex có bao gồm dấu '#' hay không
    val color = if (hexColor.startsWith("#")) hexColor.substring(1) else hexColor

    // Kiểm tra xem mã màu hex có độ dài hợp lệ (6 hoặc 8 ký tự)
    if (color.length != 6 && color.length != 8) {
        throw IllegalArgumentException("Invalid hex color length. Must be 6 or 8 characters.")
    }

    // Tính toán giá trị alpha từ độ mờ đục (opacity)
    val alpha = (opacity * 255).toInt().coerceIn(0, 255)

    // Chuyển đổi giá trị alpha thành chuỗi hex có 2 ký tự
    val alphaHex = alpha.toString(16).padStart(2, '0')

    // Kết hợp giá trị alpha hex với mã màu hex
    return "#$alphaHex$color"
}