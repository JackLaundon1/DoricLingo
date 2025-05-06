package com.example.doriclingo.model

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.doriclingo.ui.theme.DoricLingoTheme
import com.example.doriclingo.widget.updateWidget

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoricLingoTheme(darkTheme = false) {
            }
        }
    }
}

