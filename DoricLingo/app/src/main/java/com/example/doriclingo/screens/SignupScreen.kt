package com.example.doriclingo.screens

import android.content.res.Configuration
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doriclingo.R
import com.example.doriclingo.interfaces.UserEvent
import com.example.doriclingo.viewmodel.state.UserState
import com.example.doriclingo.util.Utility
import com.example.doriclingo.util.isNetworkAvailable
import com.example.doriclingo.viewmodel.FirebaseAuthenticationViewModel
import com.example.doriclingo.widget.updateWidget
import kotlinx.coroutines.launch


@Composable
fun SignupScreen(
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authenticationViewModel: FirebaseAuthenticationViewModel = viewModel()
) {
    val context = LocalContext.current
    val orientation = LocalConfiguration.current.orientation

    //variables to store user input
    var validName by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var loading by rememberSaveable { mutableStateOf(false) }
    var validEmail by rememberSaveable { mutableStateOf(false) }
    var validPassword by rememberSaveable { mutableStateOf(false) }
    var validConfirmPassword by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    //portrait mode layout
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            //centres the content
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(context.getString(R.string.signup))

            //name input field
            OutlinedTextField(
                value = state.name, onValueChange = {
                    name = it
                    //validates the name
                    validName = validateName(it)
                    //update the state with name
                    onEvent(UserEvent.SetName(it))
                },
                label = { Text(context.getString(R.string.name)) },
                //takes full width of the container
                modifier = Modifier.fillMaxWidth()
            )

            //email input field
            OutlinedTextField(
                value = state.email, onValueChange = {
                    email = it
                    //validates email
                    validEmail = validateEmail(it)
                    //update the state with the email
                    onEvent(UserEvent.SetEmail(it))
                },
                label = { Text(context.getString(R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )

            //error message if email is invalid
            if (email.isNotEmpty() && !validEmail) {
                Text(context.getString(R.string.invalid_email), color = colorResource(R.color.red))
            }

            //password input field
            OutlinedTextField(
                value = password, onValueChange = {
                    password = it
                    //validates the password
                    //the password is not stored in the state for security reasons
                    validPassword = validatePassword(it)
                },
                label = { Text(context.getString(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            //error message if the password is not valid
            if (password.isNotEmpty() && !validPassword) {
                Text(context.getString(R.string.password_policy), color = colorResource(R.color.red))
            }

            OutlinedTextField(
                value = confirmPassword, onValueChange = {
                    confirmPassword = it
                    validConfirmPassword = (password == it)
                },
                label = { Text(context.getString(R.string.confirm_password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            //if the password and confirm passwords don't match
            if (password != confirmPassword) {
                Text(context.getString(R.string.passwords_must_match), color = colorResource(R.color.red))
            }

            Spacer(modifier = Modifier.height(20.dp))

            //submit button
            Button(
                onClick = {
                    scope.launch{
                        //if the network is available
                        if (isNetworkAvailable(context)) {
                            if (password == confirmPassword && validName && validEmail && validPassword) {
                                //disable the button
                                loading = true
                                authenticationViewModel.signup(name, email, password, confirmPassword) { success, errorMessage ->
                                    //disables the loading button once signup finishes
                                    loading = false
                                    if (success) {
                                        Utility.showToast(context, context.getString(R.string.account_created))
                                        updateWidget(context)
                                        //saves user
                                        onEvent(UserEvent.SaveUser)
                                        //navigates to home screen
                                        navController.navigate(Screen.Home.route)
                                        //clears the back stack
                                        navController.popBackStack(Screen.Login.route, inclusive = true)
                                    } else {
                                        Utility.showToast(context, errorMessage ?: context.getString(R.string.error_message))
                                    }
                                }
                            } else {
                                Utility.showToast(context, context.getString(R.string.input_fields))
                            }
                        }else{
                            Utility.showToast(context, context.getString(R.string.no_internet))
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = if (loading) context.getString(R.string.creating_account) else context.getString(R.string.create_account))
            }
        }
    } else
    //landscape orientation layout
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                //used to scroll down when in landscape mode
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // Padding between row and button
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                //left column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.name, onValueChange = {
                            name = it
                            validName = validateName(it)
                            onEvent(UserEvent.SetName(it))
                        },
                        label = { Text(context.getString(R.string.name)) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = password, onValueChange = {
                            password = it
                            validPassword = validatePassword(it)
                        },
                        label = { Text(context.getString(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (password.isNotEmpty() && !validPassword) {
                        Text(context.getString(R.string.password_policy), color = colorResource(R.color.red))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp)) // Horizontal space between columns

                //right column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.email, onValueChange = {
                            email = it
                            validEmail = validateEmail(it)
                            onEvent(UserEvent.SetEmail(it))
                        },
                        label = { Text(context.getString(R.string.email)) }
                    )

                    if (email.isNotEmpty() && !validEmail) {
                        Text(context.getString(R.string.invalid_email), color = colorResource(R.color.red))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = confirmPassword, onValueChange = {
                            confirmPassword = it
                            validConfirmPassword = (password == it)
                        },
                        label = { Text(context.getString(R.string.confirm_password)) },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (password != confirmPassword) {
                        Text(context.getString(R.string.passwords_must_match), color = colorResource(R.color.red))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp)) // Space between row and button

            Button(
                onClick = {
                    scope.launch{
                        if (isNetworkAvailable(context)) {
                            if (password == confirmPassword && validName && validEmail && validPassword) {
                                loading = true
                                authenticationViewModel.signup(name, email, password, confirmPassword) { success, errorMessage ->
                                    loading = false
                                    if (success) {
                                        Utility.showToast(context, context.getString(R.string.account_created))
                                        updateWidget(context)
                                        onEvent(UserEvent.SaveUser)
                                        navController.navigate(Screen.Home.route)
                                        navController.popBackStack(Screen.Login.route, inclusive = true)
                                    } else {
                                        Utility.showToast(context, errorMessage ?: context.getString(R.string.error_message))
                                    }
                                }
                            } else {
                                Utility.showToast(context, context.getString(R.string.input_fields))
                            }
                        }else{
                            Utility.showToast(context, context.getString(R.string.no_internet))
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally) // Center the button horizontally
            ) {
                Text(text = context.getString(R.string.create_account))
            }
        }
    }
}


//functions for input validation
fun validateName(name: String): Boolean {
    // Ensures only letters, apostrophes, or hyphens can be entered.
    return name.matches("^[a-zA-Z]+(?:[-' ][a-zA-Z]+)*$".toRegex())
}

fun validateEmail(email: String): Boolean {
    // Uses the Patterns API to ensure the email format is valid
    // Also trims whitespace from the email
    return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}

fun validatePassword(password: String): Boolean {
    // Enforces the password policy
    // Sets a list of special characters allowed in passwords
    val specialCharacters = setOf('!', '£', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '{', '}', '[', ']', ':', '@', '~', '#', ';', '<', '>', ',', '.', '/', '\'', '"')

    return password.length >= 8 && password.any { it.isUpperCase() } && password.any { it.isLowerCase() } && password.any { it.isDigit() }
}

