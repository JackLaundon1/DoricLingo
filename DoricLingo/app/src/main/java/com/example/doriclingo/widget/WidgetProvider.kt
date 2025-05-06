package com.example.doriclingo.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.google.firebase.auth.FirebaseAuth

//extends android's app widget provider
class WidgetProvider : AppWidgetProvider(){
    //called when the widget is updated
    //accepts context, widget manager, and array of widget IDs
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, widgetIds: IntArray) {
        //loops through all widgets that need to be updated
        for (widgetId in widgetIds) {
            //calls the update widget function
            updateWidget(context, appWidgetManager, widgetId)
        }
    }
    }
