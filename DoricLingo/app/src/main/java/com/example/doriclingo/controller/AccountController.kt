package com.example.doriclingo

import android.content.Context
import androidx.navigation.NavHostController
import com.example.doriclingo.util.Utility
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.doriclingo.screens.Screen
import com.example.doriclingo.widget.updateWidget

//class used to handle account operations
class AccountController(
    private val context: Context,
    private val navController: NavHostController
) {
    //logout function
    fun logout() {
        //uses firebase sign out method
        FirebaseAuth.getInstance().signOut()
        //updates the widget and navigates to the account screen
        updateWidget(context)
        navController.navigate(Screen.Account.route) {
            //clears the back stack
            popUpTo(Screen.Account.route) { inclusive = true }
        }
    }


    //delete account function
    fun deleteAccount(onSuccess:() -> Unit, onFailure: (Exception) -> Unit){
        //gets current user ID
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        //gets instance of the firestore database
        val firestore = FirebaseFirestore.getInstance()

        //deletes from user collection from firebase
        firestore.collection("users").document(userId ?: "").delete()
            .addOnSuccessListener {
                //deletes from progress collectino if successful
                firestore.collection("progress").document(userId ?: "").delete()
                    .addOnSuccessListener {
                        Utility.showToast(context, context.getString(R.string.firebase_data_deleted))
                        onSuccess()
                    }
                    .addOnFailureListener{ e->
                        Utility.showToast(context, context.getString(R.string.delete_data_error))
                    }
            }
            .addOnFailureListener{ e->
                Utility.showToast(context, context.getString(R.string.delete_data_error))
                onFailure(e)
            }

    }


    //reauthenticate function
    fun reAuthenticate(email: String, password: String, onSuccess: () -> Unit) {
        //gets current user ID
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            //reauthenticates using built in firebase method
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    //notifies success
                    onSuccess()
                }
                .addOnFailureListener {
                    Utility.showToast(context, context.getString(R.string.reauth_error) + {it.message})
                }
        } else {
            Utility.showToast(context, context.getString(R.string.user_not_found))
        }
    }


}
