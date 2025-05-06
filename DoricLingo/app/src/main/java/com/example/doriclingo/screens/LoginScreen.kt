package com.example.doriclingo.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.doriclingo.components.ForgotPasswordDialog
import com.example.doriclingo.R
import com.example.doriclingo.interfaces.UserEvent
import com.example.doriclingo.viewmodel.state.UserState
import com.example.doriclingo.util.Utility
import com.example.doriclingo.util.isNetworkAvailable
import com.google.firebase.auth.FirebaseAuth
import com.example.doriclingo.viewmodel.FirebaseAuthenticationViewModel
import com.example.doriclingo.widget.updateWidget
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authenticationViewModel: FirebaseAuthenticationViewModel
) {
    val context = LocalContext.current
    //stores email input
    var email by rememberSaveable { mutableStateOf("") }
    //stores password input
    var password by rememberSaveable { mutableStateOf("") }
    //indicates login state
    var loading by rememberSaveable { mutableStateOf(false) }
    //controls visibility of forgot password dialog
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    //gets current screen orientation
    val orientation = LocalConfiguration.current.orientation

    //layout for portrait orientation
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = context.getString(R.string.login))

            //email input field
            OutlinedTextField(
                value = email,
                onValueChange = { email = sanitiseEmail(it) },
                label = { Text(text = context.getString(R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )

            //password input field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = context.getString(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                //hides password from view
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(20.dp))
            //login button
            Button(
                onClick = {
                    scope.launch {
                        //if internet is available
                        if (isNetworkAvailable(context)) {

                            loading = true
                            //login with firebase
                            authenticationViewModel.login(
                                email,
                                password
                            ) { success, errorMessage ->
                                loading = false
                                if (success) {
                                    Utility.showToast(
                                        context,
                                        context.getString(R.string.login_successful)
                                    )
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    if (currentUser != null) {
                                        //loads user info from firebase
                                        authenticationViewModel.fetchUserData(currentUser.uid) { success, name, email ->
                                            if (success) {
                                                state.name = name ?: ""
                                                state.email = email ?: ""
                                                onEvent(UserEvent.SaveUser)
                                                //updates the widget for current user
                                                updateWidget(context)
                                            } else {
                                                Utility.showToast(
                                                    context,
                                                    context.getString(R.string.user_not_found)
                                                )
                                            }
                                        }
                                    }
                                    //navigates to home screen
                                    navController.navigate(Screen.Home.route) {
                                        //clears the back stack
                                        popUpTo(Screen.Account.route) { inclusive = true }
                                    }
                                } else {
                                    Utility.showToast(
                                        context,
                                        errorMessage ?: context.getString(R.string.error_message)
                                    )
                                }
                            }
                        } else {
                            Utility.showToast(context, context.getString(R.string.no_internet))
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                //changes text on login button
                Text(text = if (loading) context.getString(R.string.loading) else context.getString(R.string.log_in))
            }

            Spacer(modifier = Modifier.height(20.dp))

            //forgot password option
            Text(
                text = context.getString(R.string.forgot_password),
                textDecoration = TextDecoration.Underline,
                //style = ButtonLinkStyle,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 10.dp)
                    .clickable { showDialog = true }
            )


            //shows forgot password dialog
            if (showDialog) {
                ForgotPasswordDialog(
                    onDismiss = { showDialog = false },
                    //uses firebase to send reset link to email address
                    onSendResetLink = { email ->
                        authenticationViewModel.sendPasswordReset(email) { success, errorMessage ->
                            if (success) {
                                Utility.showToast(context, context.getString(R.string.password_reset) + email)
                                showDialog = false
                            } else {
                                Utility.showToast(context, errorMessage ?: context.getString(R.string.error_message))
                            }
                        }
                    }
                )
            }
        }
    }
    //landscape orientation with side by side layout
    else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            //left column (input fields)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = context.getString(R.string.email)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = context.getString(R.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            //right column (login and forgot password)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.forgot_password),
                    color = colorResource(R.color.link),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { showDialog = true }
                        .padding(bottom = 20.dp)
                )

                Button(
                    onClick = {
                        loading = true
                        authenticationViewModel.login(email, password) { success, errorMessage ->
                            loading = false
                            if (success) {
                                Utility.showToast(context, context.getString(R.string.login_successful))
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                updateWidget(context)
                                if (currentUser != null) {
                                    authenticationViewModel.fetchUserData(currentUser.uid) { success, name, email ->
                                        if (success) {
                                            state.name = name ?: ""
                                            state.email = email ?: ""
                                            onEvent(UserEvent.SaveUser)
                                        } else {
                                            Utility.showToast(context, context.getString(R.string.user_not_found))
                                        }
                                    }
                                }
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Account.route) { inclusive = true }
                                }
                            } else {
                                Utility.showToast(context, errorMessage ?: context.getString(R.string.error_message))
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(text = if (loading) context.getString(R.string.loading) else context.getString(R.string.log_in))
                }
            }


            if (showDialog) {
                ForgotPasswordDialog(
                    onDismiss = { showDialog = false },
                    onSendResetLink = { email ->
                        authenticationViewModel.sendPasswordReset(email) { success, errorMessage ->
                            if (success) {
                                Utility.showToast(context, context.getString(R.string.password_reset) + email)
                                showDialog = false
                            } else {
                                Utility.showToast(context, errorMessage ?: context.getString(R.string.error_message))
                            }
                        }
                    }
                )
            }
        }

    }
}



//functions to sanitise input

fun sanitiseEmail(email:String):String{
    //removes trailing and following whitespace and puts everything into lowercase
    return email.trim().lowercase()
}


