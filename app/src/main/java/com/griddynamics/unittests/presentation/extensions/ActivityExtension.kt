package com.griddynamics.unittests.presentation.extensions

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard(view: View) {
    try {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    } catch (e: Exception) {
        Log.d("${this::class.java}", "Could not hide a keyboard")
    }
}