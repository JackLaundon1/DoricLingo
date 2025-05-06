package com.example.doriclingo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.doriclingo.R
import com.example.doriclingo.ui.theme.DoricLingoTheme
import com.example.doriclingo.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {
    //remembers the current dark theme state
    val darkTheme by themeViewModel.isDarkTheme
    val context = LocalContext.current

    //toggles state for the Switch (manual theme setting)
    var switchState by remember { mutableStateOf(darkTheme) }
    if (switchState != darkTheme) {
        themeViewModel.toggleTheme()
    }


    //layout
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(context.getString(R.string.adjust_theme), style = MaterialTheme.typography.headlineMedium)
            //switch to change the theme
                Switch(
                    checked = switchState,
                    //updates switch state
                    onCheckedChange = { newState ->
                        switchState = newState
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                //display
                Text(
                    text = if (darkTheme) context.getString(R.string.dark_mode) else context.getString(R.string.light_mode),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
