package com.example.mad_3

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mad_3.databinding.ActivityReminderFragmentBinding
import java.util.*

class ReminderFragment : Fragment() {

    private lateinit var binding: ActivityReminderFragmentBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityReminderFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize AlarmManager
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set Reminder Button
        binding.btnSetReminder.setOnClickListener {
            setReminder()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setReminder() {
        // Get user input
        val title = binding.etReminderTitle.text.toString()
        val datePicker = binding.datePicker
        val timePicker = binding.timePicker
        val calendar = Calendar.getInstance()

        // Set Calendar to user-selected date and time
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, timePicker.hour, timePicker.minute, 0)

        // Set up the Intent and PendingIntent
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminderTitle", title)
        }
        pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        // Set Alarm with AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user or show a message
                Toast.makeText(context, "Please enable exact alarms", Toast.LENGTH_SHORT).show()
                return
            }
//            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//            startActivity(intent)
        }


        // Notify the user
        Toast.makeText(requireContext(), "Reminder Set!", Toast.LENGTH_SHORT).show()
    }
}
