package com.example.doriclingo.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import com.example.doriclingo.R
import com.example.doriclingo.screens.DatabaseProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//updates the widget with the progress per user
fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
    //creates remote views object to represent the widget
    val views = RemoteViews(context.packageName, R.layout.widget)

    //starts a coroutine on the IO dispatcher (background thread)
    CoroutineScope(Dispatchers.IO).launch {
        //gets the current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        //if there is no user logged in
        if(userId.isNullOrEmpty()){
            //switches to the main thread to update the widget
           withContext(Dispatchers.Main){
               //shows "no progress saved"
               views.setTextViewText(R.id.widget_title, context.getString(R.string.no_progress_saved))
               views.setTextViewText(R.id.widget_content, "")

               //updates the widget
               appWidgetManager.updateAppWidget(widgetId, views)
           }
        }
        //accesses the room db
        val db = DatabaseProvider.getDatabase(context)
        val progressData = db.progressDao.getProgress(userId)

        //loops through the flow to get the progress data
        progressData.collect { progressEntity ->
            //changes to the main thread to update the UI
            withContext(Dispatchers.Main) {
                if (userId.isEmpty()) {
                    //no user logged in
                    views.setTextViewText(R.id.widget_title, context.getString(R.string.no_progress_saved))
                }
                //if the user has progress saved
                else if (progressEntity != null) {
                    //displays the progress in the widget
                    val progress = (progressEntity.lastConversation * 10).toInt()
                    views.setTextViewText(R.id.widget_title, context.getString(R.string.your_progress))
                    views.setTextViewText(R.id.widget_content, "$progress%")
                }
                //if the user is logged in but has no progress saved
                else {
                    //shows 0% progress
                    views.setTextViewText(R.id.widget_title, context.getString(R.string.your_progress))
                    views.setTextViewText(R.id.widget_content, "0%")
                }
                //updates the widget
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }

    }
}