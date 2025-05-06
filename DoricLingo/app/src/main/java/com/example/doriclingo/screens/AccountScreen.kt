package com.example.doriclingo.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doriclingo.AccountController
import com.example.doriclingo.R
import com.example.doriclingo.interfaces.UserEvent
import com.example.doriclingo.util.Utility
import com.example.doriclingo.ui.themes.DangerButton
import com.example.doriclingo.viewmodel.state.UserState
import com.example.doriclingo.ui.themes.ThemedButton
import com.google.firebase.auth.FirebaseAuth
import components.ReauthDialog
import com.example.doriclingo.viewmodel.FirebaseAuthenticationViewModel
import com.example.doriclingo.widget.updateWidget

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    darkTheme: Boolean
) {
    val context = LocalContext.current
    //accesses the controller to handle login/out
    val controller = remember { AccountController(context, navController) }
    //controls visibility of reauthentication dialog
    var showDialog by rememberSaveable { mutableStateOf(false) }

    //gets the firebase authentication viewmodel
    val authenticationViewModel: FirebaseAuthenticationViewModel = viewModel()

    //gets current user ID from firebase
    val user = FirebaseAuth.getInstance().currentUser
    //access the room db
    val db = DatabaseProvider.getDatabase(context)
    //accesses the user from the room db
    val userRoom by db.dao.getUser(user?.uid ?: "").collectAsState(initial = null)

    //layout
    Column(modifier = modifier.fillMaxSize()
        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        verticalArrangement = Arrangement.Center
        ) {
        if (user == null) {
            //if the user is not signed in
            //login button
            ThemedButton(
                onClick = { navController.navigate(Screen.Login.route) },
                darkTheme = darkTheme,
                text = context.getString(R.string.login),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            //sign up button
            ThemedButton(
                onClick = { navController.navigate(Screen.Signup.route) },
                darkTheme = darkTheme,
                text = context.getString(R.string.signup),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                isSecondary = true
            )

        } else {
            //if the user is signed in
            val username = userRoom?.firstOrNull()?.name

            //displays the user's name from room db
            Text(context.getString(R.string.welcome, username),
                //centres the text and adds space between the text and buttons
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(bottom = 40.dp),
                //styles the text like a heading
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            //logout button
            ThemedButton(
                onClick = { controller.logout() },
                darkTheme = darkTheme,
                text =  context.getString(R.string.log_out),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                isSecondary = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            //delete account button
            DangerButton(
                onClick = { showDialog = true },
                text = context.getString(R.string.delete_account),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp),
            )


            //shows the reauthentication dialog
            if(showDialog){
                ReauthDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { email: String, password: String ->
                        //reauthenticates user
                        controller.reAuthenticate(email, password) {
                            //deletes the user data from firebase
                            controller.deleteAccount(
                                //only proceeds if deleteAccount successfully completes
                                onSuccess = {
                                    controller.logout()
                                    updateWidget(context)

                                    onEvent(UserEvent.DeleteUser)
                                    //reloads the screen and clears the back stack
                                    navController.navigate(Screen.Account.route) {
                                        popUpTo(Screen.Account.route) { inclusive = true }
                                    }
                                },
                                //if deleteAccount fails
                                onFailure = { error ->
                                    Utility.showToast(context, context.getString(R.string.delete_data_error) + error.message)
                                }
                            )
                        }
                        //hides the dialog
                        showDialog = false
                    }
                )
            }

        }
    }
}

