//view model is used as it holds data if there are config changes
package com.example.doriclingo.viewmodel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.doriclingo.room.entity.UserEntity
import com.example.doriclingo.widget.updateWidget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow

//extends viewmodel
class FirebaseAuthenticationViewModel : ViewModel() {

    //gets the firebase authentication instance
    private val auth = Firebase.auth
    //gets Firestore instance
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //connects to the firestore database
    private val firestore = Firebase.firestore


    //checks if the user is logged in
    private val loggedIn = MutableStateFlow(FirebaseAuth.getInstance().currentUser != null)
    val isLoggedIn: MutableStateFlow<Boolean> = loggedIn


    //function to handle the sign up process
    fun signup(name: String, email: String,password: String, confirmPassword: String, onResult: (Boolean, String?) ->Unit ) {
        //creates a user for the database
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                //if signing up succeeds
                if (it.isSuccessful) {
                    //creates an ID to identify users in the database
                    val userID = it.result?.user?.uid

                    //creates the user model
                    val userEntity = UserEntity(name, email, userID!!)
                    //stores the user ID in the database
                    firestore.collection("users").document(userID)
                        .set(userEntity)
                        .addOnCompleteListener { store ->
                            if (store.isSuccessful) {

                                //creates the progress collection in firestore
                                val userProgress = mapOf(
                                    "id" to userID,
                                    "conversation" to 0,
                                    "last_conversation" to 0
                                )
                                //adds the progress to the database
                                firestore.collection("progress").document(userID)
                                    .set(userProgress)
                                    .addOnCompleteListener{
                                        if(it.isSuccessful){
                                            //successful sign up
                                            onResult(true, null)
                                        }else{
                                            onResult(false, "Error creating database document.")
                                        }
                                    }
                                onResult(true, null)
                            } else {
                                onResult(false, "Error")
                            }
                        }
                } else{
                    //exception message
                    it.exception?.localizedMessage?.let { it1 -> onResult(false, it1) }
                }
            }
    }


    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit){
        //signs the user in
        auth.signInWithEmailAndPassword(email, password)
            //triggers when sign in is complete
            .addOnCompleteListener{
                if(it.isSuccessful){
                    onResult(true, null)
                }else{
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }

    fun fetchUserData(userId: String, onResult: (Boolean, String?, String?) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name")
                    val email = document.getString("email")
                    onResult(true, name, email)
                } else {
                    onResult(false, null, null)
                }
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}
