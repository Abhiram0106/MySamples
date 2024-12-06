package com.example.mysamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.mysamples.ai_text_load.AITextLoad
import com.example.mysamples.image_cropper.SimpleImageCropper
import com.example.mysamples.ui.theme.MySamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySamplesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    val color = Color(255,252,3,255)
//                    Box(modifier = Modifier.padding(innerPadding).background(color)) {
//                        SimpleImageCropper()
//                    }
                    AITextLoad(text = "Hello world ".repeat(10), modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}