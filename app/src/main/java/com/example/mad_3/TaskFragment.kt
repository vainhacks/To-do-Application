package com.example.mad_3

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private var taskList = mutableListOf<Task>()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TaskFragment", "TaskAdapter initialized")

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

        // Load tasks from SharedPreferences
        loadTasks()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_task_fragment, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewTasks)
        val fabAddTask: FloatingActionButton = view.findViewById(R.id.fabAddTask)

        // Set up RecyclerView with LinearLayoutManager to ensure scrolling
        recyclerView.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(taskList) { task -> onTaskClick(task) }
        recyclerView.adapter = taskAdapter

        // Floating Action Button to add new task
        fabAddTask.setOnClickListener {
            Log.d("TaskFragment", "Floating Action Button clicked")
            showAddTaskDialog()
        }

        return view
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Task")

        val input = EditText(context)
        input.hint = "Enter task title"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val taskTitle = input.text.toString()
            if (taskTitle.isNotEmpty()) {
                taskList.add(Task(taskTitle))
                taskAdapter.notifyDataSetChanged()

                // Save tasks to SharedPreferences
                saveTasks()

                dialog.dismiss()
            } else {
                Toast.makeText(context, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_edittext)

        dialog.show()
    }

    private fun onTaskClick(task: Task) {
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showEditTaskDialog(task)
                1 -> deleteTask(task)
            }
        }
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_edittext)

        dialog.show()
    }

    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Task")

        val input = EditText(context)
        input.setText(task.title)
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val updatedTitle = input.text.toString()
            if (updatedTitle.isNotEmpty()) {
                task.title = updatedTitle
                taskAdapter.notifyDataSetChanged()

                // Save tasks to SharedPreferences
                saveTasks()

                dialog.dismiss()
            } else {
                Toast.makeText(context, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_edittext)

        dialog.show()
    }

    private fun deleteTask(task: Task) {
        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()

        // Save tasks to SharedPreferences
        saveTasks()
    }

    // Function to save tasks to SharedPreferences
    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(taskList)
        editor.putString("task_list", json)
        editor.apply()

        // Trigger widget update
        val intent = Intent(requireContext(), TaskWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(requireContext())
            .getAppWidgetIds(ComponentName(requireContext(), TaskWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireContext().sendBroadcast(intent)
    }

    // Function to load tasks from SharedPreferences
    private fun loadTasks() {
        val json = sharedPreferences.getString("task_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = gson.fromJson(json, type)
        }
    }

}
