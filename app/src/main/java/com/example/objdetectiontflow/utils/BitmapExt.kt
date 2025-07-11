package com.example.objdetectiontflow.utils

import android.graphics.Bitmap

fun Bitmap.centerCrop(desireWidth: Int, desireHeight: Int): Bitmap {
    val xStart = (width - desireWidth) / 2
    val yStart = (height - desireHeight) / 2

    if (xStart < 0 || yStart < 0 || desireWidth > width || desireHeight > height) {
        throw IllegalArgumentException("Invalid arguments for Center Cropping!")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desireWidth, desireHeight)
}