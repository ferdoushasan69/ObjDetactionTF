package com.example.objdetectiontflow.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.objdetectiontflow.domain.Classification
import com.example.objdetectiontflow.domain.LandMarkClassifier
import com.example.objdetectiontflow.utils.centerCrop

class LandMarkImageAnalyzer(
    private val classifier: LandMarkClassifier,
    private val onResult : (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer{

    private var frameSkipCounter = 0
    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 60 == 0 ){
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitMap = image.toBitmap().centerCrop(321,321)

            val classificationResult = classifier.classifyCurrentFrame(
                bitMap,
                rotation = rotationDegrees
            )
            onResult(classificationResult)
        }
        frameSkipCounter++
        image.close()
    }

}