package com.example.mad_3

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ensures smooth edge-to-edge design
        setContentView(R.layout.activity_main)

        // Set the com.example.mad_3.TaskFragment as default
        loadFragment(TaskFragment())

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment = TaskFragment()
            when (item.itemId) {
                R.id.nav_tasks -> selectedFragment = TaskFragment()
                R.id.nav_timer -> selectedFragment = TimerFragment()
                R.id.nav_reminders -> selectedFragment = ReminderFragment()
            }
            loadFragment(selectedFragment)
            true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }



    }

    private fun loadFragment(fragment: Fragment) {
        // Load the given fragment into the frame layout container
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
