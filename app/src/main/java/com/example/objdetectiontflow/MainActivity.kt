package com.example.objdetectiontflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.objdetectiontflow.data.LandMarkClassificationImpl
import com.example.objdetectiontflow.domain.Classification
import com.example.objdetectiontflow.presentation.CameraPreview
import com.example.objdetectiontflow.presentation.LandMarkImageAnalyzer
import com.example.objdetectiontflow.presentation.ui.theme.ObJDetectionTFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isCameraPermissionGranted()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
            return
        }
        setMainContent()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setMainContent()
        }
    }

    private fun setMainContent() {
        setContent {
            ObJDetectionTFlowTheme {
                var classification by remember { mutableStateOf(emptyList<Classification>()) }

                val imageAnalyzer = remember {
                    LandMarkImageAnalyzer(
                        classifier = LandMarkClassificationImpl(
                            context = this,
                        ),
                        onResult = {
                            classification = it
                        }
                    )
                }

                val cameraPreviewController = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(applicationContext),
                            imageAnalyzer
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        controller = cameraPreviewController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(.7f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(.3f)
                            .align(alignment = Alignment.BottomCenter)
                    ) {
                        LazyColumn (modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp)){
                           items(classification) {
                               Item(
                                   name = it.objName,
                                   score = it.score
                               )
                           }
                    }
                    }
                }
            }
        }
    }

    @Composable
    fun Item(modifier: Modifier = Modifier,name : String,score : Float) {
        Column {
            Text(
                text = name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.inversePrimary
                    )
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(18.dp))
            Text(
                text = "${score}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()

                    .background(
                        MaterialTheme.colorScheme.inversePrimary
                    )
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }

    }
    private fun isCameraPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
