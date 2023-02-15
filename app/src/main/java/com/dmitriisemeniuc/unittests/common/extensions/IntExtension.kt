package com.dmitriisemeniuc.unittests.common.extensions

import android.content.res.Resources

fun Int.dpToPixel(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}