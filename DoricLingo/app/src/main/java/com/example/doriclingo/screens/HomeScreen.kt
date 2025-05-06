package com.example.doriclingo.screens

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doriclingo.R
import com.example.doriclingo.interfaces.UserEvent
import com.example.doriclingo.ui.themes.ThemedButton
import com.example.doriclingo.viewmodel.state.UserState
import com.example.doriclingo.components.CourseProgressBar
import com.example.doriclingo.ui.themes.DangerButton
import com.example.doriclingo.util.Utility
import com.example.doriclingo.util.isNetworkAvailable
import com.example.doriclingo.viewmodel.CourseProgressViewModel
import com.example.doriclingo.viewmodel.FirebaseAuthenticationViewModel
import components.ResetProgressDialog
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    darkTheme: Boolean
) {
    val context = LocalContext.current
    //gets firebase authentication view model
    val authViewModel: FirebaseAuthenticationViewModel = viewModel()
    //gets progress viewmodel
    val progressViewModel: CourseProgressViewModel = viewModel()
    //controls visibility of reset progress dialog
    var showDialog by rememberSaveable { mutableStateOf(false) }

    //collects the current logged in state and course progress
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val courseProgress by progressViewModel.courseProgress.collectAsStateWithLifecycle()

    //allows for scrolling if the screen is horizontal
    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    //layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            //scrolling allows the content to be viewed when landscape
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        //if the user is logged in
        if (isLoggedIn) {
            //displays the progress bar
            CourseProgressBar(courses = courseProgress)

            //reset progress button
            DangerButton(
                onClick = {
                    scope.launch {
                        //if there is internet connection, show the reset progress dialog
                        if (isNetworkAvailable(context)) {
                            showDialog = true
                        }else{
                            Utility.showToast(context, context.getString(R.string.no_internet))
                        }
                    }
                },
                text = context.getString(R.string.reset_progress_button),
                modifier = Modifier
                    .height(60.dp)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
            )
        }
        //if the user is not logged in
        else {
            Text(
                text = context.getString(R.string.no_progress),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 20.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = context.getString(R.string.ready_to_learn),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (darkTheme) colorResource(R.color.white) else colorResource(R.color.black),
                fontSize = 20.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        //centered buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //takes the user to the training screen
            ThemedButton(
                onClick = { navController.navigate(Screen.Training.route) },
                darkTheme = darkTheme,
                text = context.getString(R.string.training),
                modifier = Modifier
                    .height(60.dp)
                    //centred with limited width
                    .fillMaxWidth(0.6f)
            )
            //takes the user to the testing screen
            ThemedButton(
                onClick = { navController.navigate(Screen.Testing.route) },
                darkTheme = darkTheme,
                text = context.getString(R.string.testing),
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(0.6f)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // push everything up evenly

        //show dialog logic
        if(showDialog) {
                ResetProgressDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        //resets the user's progress
                        progressViewModel.resetProgress { success, message ->
                            if (success) {
                                showDialog = false
                            } else {
                                Utility.showToast(context, context.getString(R.string.error_saving))
                            }
                        }
                        showDialog = false
                    }
                )
        }
    }
}



