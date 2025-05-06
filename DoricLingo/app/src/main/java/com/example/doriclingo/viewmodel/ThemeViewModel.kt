package com.example.doriclingo.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


//extends ViewModel
class ThemeViewModel : ViewModel(){
    var isDarkTheme = mutableStateOf(false)

    //toggles the theme
    fun toggleTheme(){
        isDarkTheme.value = !isDarkTheme.value
    }
}