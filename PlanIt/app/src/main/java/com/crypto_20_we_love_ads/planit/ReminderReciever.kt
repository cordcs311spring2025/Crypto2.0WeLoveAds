package com.crypto_20_we_love_ads.planit

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.app.AlarmManager
import android.os.SystemClock



class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d("ReminderReceiver", "Notification permission granted")

            val dbHelper = DatabaseHelper(context)
            val db: SQLiteDatabase = dbHelper.readableDatabase

            val cursor = db.rawQuery("SELECT id, title, startDate, startTime, reminder1 FROM Calendar", null)
            val now = Calendar.getInstance()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now.time)

            val reminderMap = mapOf(
                "5 Minutes" to 5,
                "10 Minutes" to 10,
                "15 Minutes" to 15,
                "20 Minutes" to 20,
                "30 Minutes" to 30,
                "45 Minutes" to 45,
                "1 Hour" to 60
            )

            try {
                while (cursor.moveToNext()) {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"))
                    val startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"))
                    val reminder = cursor.getString(cursor.getColumnIndexOrThrow("reminder1"))

                    Log.d("ReminderReceiver", "Checking event: $title at $startDate $startTime with reminder $reminder")

                    if (startDate == currentDate && reminderMap.containsKey(reminder)) {
                        val minutesBefore = reminderMap[reminder] ?: continue

                        val eventTime = Calendar.getInstance().apply {
                            time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse("$startDate $startTime")!!
                        }

                        val reminderTime = Calendar.getInstance().apply {
                            time = eventTime.time
                            add(Calendar.MINUTE, -minutesBefore)
                        }

                        Log.d("ReminderReceiver", "Reminder time: ${reminderTime.time}")

                        if (now.after(reminderTime) && now.before(Calendar.getInstance().apply {
                                timeInMillis = reminderTime.timeInMillis + 60000
                            })) {
                            Log.d("ReminderReceiver", "Sending notification for: $title ($reminder before)")
                            sendNotification(context, title, startTime, reminder)
                        } else {
                            Log.d("ReminderReceiver", "Not time yet for: $title ($reminder before)")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ReminderReceiver", "Error processing events: ${e.message}")
            } finally {
                cursor.close()
                db.close()
            }
        } else {
            Log.d("ReminderReceiver", "Notification permission not granted.")
        }
    }

    // Function to set up the repeating alarm
    fun setRepeatingAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)

        // Create a PendingIntent that will trigger the ReminderReceiver
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val triggerAtMillis = SystemClock.elapsedRealtime() + 1 * 60 * 1000
        val intervalMillis = 1 * 60 * 1000

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            intervalMillis.toLong(),
            pendingIntent
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(context: Context, title: String, time: String, reminder: String) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("event_reminders", "Event Reminders", NotificationManager.IMPORTANCE_HIGH)
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "event_reminders")
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("$title in $reminder")
            .setContentText("Starts at $time")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
