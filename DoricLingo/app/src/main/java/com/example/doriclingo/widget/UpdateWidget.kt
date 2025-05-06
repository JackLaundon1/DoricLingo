package com.example.doriclingo.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent


fun updateWidget(context: Context){
    //creates intent to trigger widget update
    val intent = Intent(context, WidgetProvider::class.java).apply {
        //sets action to system update
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        //retrieve the widget ID
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
        //pass widget ID into the intent extras
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    }
    //sends broadcast with widget intent to the widget provider
    context.sendBroadcast(intent)

}