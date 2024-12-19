package com.example.mad_3

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Perform this loop procedure for each widget
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_task)

            // Get tasks from SharedPreferences
            val sharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
            val json = sharedPreferences.getString("task_list", null)

            // Deserialize task list as a list of Task objects, not strings
            val taskList: List<Task> = if (json != null) {
                val type = object : TypeToken<List<Task>>() {}.type
                Gson().fromJson(json, type)
            } else {
                emptyList()
            }

            // Set task titles on the widget
            views.setTextViewText(R.id.taskItem1, taskList.getOrNull(0)?.title ?: "No Task")
            views.setTextViewText(R.id.taskItem2, taskList.getOrNull(1)?.title ?: "")
            views.setTextViewText(R.id.taskItem3, taskList.getOrNull(2)?.title ?: "")

            // Set up a PendingIntent to launch the app when the widget is clicked
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widgetTitle, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
