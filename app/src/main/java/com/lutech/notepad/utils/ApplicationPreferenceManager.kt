package com.lutech.notepad.utils

import android.content.Context
import android.content.SharedPreferences
import com.lutech.notepad.constants.IS_FIRST_TIME

class ApplicationPreferenceManager(
    private val preferName: String,
    val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preferName, Context.MODE_PRIVATE)


    fun putString(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun isFirstTime(): Boolean {
        return  sharedPreferences.getBoolean(IS_FIRST_TIME, true)
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun remove(key: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }
}