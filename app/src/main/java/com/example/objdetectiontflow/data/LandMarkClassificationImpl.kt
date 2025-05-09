package com.example.objdetectiontflow.data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.objdetectiontflow.domain.Classification
import com.example.objdetectiontflow.domain.LandMarkClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class LandMarkClassificationImpl(
    private val context: Context,
    private val confidenceThresholds: Float = 0.5f,
    private val maxResult: Int = 4
) : LandMarkClassifier {

    private var imageClassifier: ImageClassifier? = null

    override fun classifyCurrentFrame(
        bitmap: Bitmap,
        rotation: Int
    ): List<Classification> {
        if (imageClassifier == null) {
            initializeImageClassifier()
        }

        val imageProcessingOptions = initializeImageProcessingOption(
            rotation = rotation
        )

        val imageProcessor: ImageProcessor = ImageProcessor.Builder().build()
        val tensorImage: TensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val classificationResult = imageClassifier?.classify(tensorImage, imageProcessingOptions)

        return classificationResult?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    objName = category.displayName,
                    score = category.score
                )
            }
        }?.distinctBy { it.objName } ?: emptyList()
    }


    private fun initializeImageClassifier() {
        val baseOption = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOption)
            .setMaxResults(maxResult)
            .setScoreThreshold(confidenceThresholds)
            .build()

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmarks.tflite",
                options
            )

        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun initializeImageProcessingOption(rotation: Int): ImageProcessingOptions {
        return ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
     return  when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }

    }
}