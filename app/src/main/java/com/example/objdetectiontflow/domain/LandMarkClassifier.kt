package com.example.objdetectiontflow.domain

import android.graphics.Bitmap

interface LandMarkClassifier {

    fun classifyCurrentFrame(bitmap: Bitmap,rotation : Int) : List<Classification>
}