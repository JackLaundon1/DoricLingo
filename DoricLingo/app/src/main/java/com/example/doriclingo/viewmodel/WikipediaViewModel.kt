package com.example.doriclingo.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doriclingo.API.RetrofitInstance
import com.example.doriclingo.R
import kotlinx.coroutines.launch

class WikipediaViewModel: ViewModel() {
    var summary by mutableStateOf("")

    init{
        getSummary()
    }

    private fun getSummary() {
        viewModelScope.launch {
            //attempts to fetch summary from API
            try {
                val response = RetrofitInstance.api.getSummary("Aberdeen")
                summary = response.extract
            } catch (e: Exception) {
                Log.d(TAG, "Error $e")
            }
        }
    }
}