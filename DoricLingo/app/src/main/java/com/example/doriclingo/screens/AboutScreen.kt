package com.example.doriclingo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doriclingo.R
import com.example.doriclingo.util.isNetworkAvailable
import com.example.doriclingo.viewmodel.WikipediaViewModel

@Composable
fun AboutScreen(){
    val viewModel: WikipediaViewModel = viewModel()
    val summary = viewModel.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        var hasNetwork by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current

        //tests for network connection
        LaunchedEffect(Unit) {
            hasNetwork = isNetworkAvailable(context)
        }

        if (summary == ""){
            //shows loading indicator
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
        }else{
            //if no network connection
            if(!hasNetwork){
                //displays no network message
                Text(
                    text = context.getString(R.string.no_internet),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(16.dp),
                )
            } else{
                //shows summary
                Text(
                    text = summary,
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }

        }
    }
}
