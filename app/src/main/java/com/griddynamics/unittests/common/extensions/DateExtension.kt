package com.griddynamics.unittests.common.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.format(pattern: String = "MMM dd, yyyy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.US)
    return dateFormat.format(time)
}