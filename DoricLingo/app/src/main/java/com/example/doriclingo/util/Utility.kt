package com.example.doriclingo.util
import android.content.Context
import android.widget.Toast

//utility object holds the show toast utility method
object Utility {
    //uses the toast feature to display messages
    fun showToast(context: Context, message: String){
        Toast.makeText(context, message,Toast.LENGTH_LONG).show()
    }

}