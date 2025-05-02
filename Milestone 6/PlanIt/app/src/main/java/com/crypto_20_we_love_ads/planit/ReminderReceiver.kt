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
import android.location.Geocoder
import android.location.Location
import android.os.SystemClock
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices


class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d("ReminderReceiver", "Notification permission granted")

            val dbHelper = DatabaseHelper(context)
            val db: SQLiteDatabase = dbHelper.readableDatabase

            val cursor = db.rawQuery("SELECT id, title, startDate, startTime, reminder1, reminder2, location FROM Calendar", null)
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
                    val reminder1 = cursor.getString(cursor.getColumnIndexOrThrow("reminder1"))
                    val reminder2 = cursor.getString(cursor.getColumnIndexOrThrow("reminder2"))
                    val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))

                    Log.d("ReminderReceiver", "Checking event: $title at $startDate $startTime with reminder $reminder1 and $reminder2")

                    if (startDate == currentDate) {
                        Log.d("ReminderReceiver", "Inside startdate == currentDate")
                        if (reminder1 == "Location Based") {
                            Log.d("ReminderReceiver", "Inside reminder1 == \"Location Based\"")
                            val minutesBefore = reminderMap[reminder2] ?: continue
                            getTravelTimeAndCheckLeaveReminder(context, startTime, location, minutesBefore, title, startDate)
                        } else if (reminderMap.containsKey(reminder1)) {
                            Log.d("ReminderReceiver", "Inside else if (reminderMap.containsKey(reminder1))")
                            val minutesBefore = reminderMap[reminder1] ?: continue
                            val eventTime = Calendar.getInstance().apply {
                                time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse("$startDate $startTime")!!
                            }
                            val reminderTime = Calendar.getInstance().apply {
                                time = eventTime.time
                                add(Calendar.MINUTE, -minutesBefore)
                            }
                            if (now.timeInMillis in reminderTime.timeInMillis until (reminderTime.timeInMillis + 60000)) {
                                Log.d("ReminderReceiver", "Inside now.timeInMillis in reminderTime.timeInMillis until (reminderTime.timeInMillis + 60000)")
                                sendNotification(context, title, startTime, reminder1)
                                Log.d("ReminderReceiver", "Notification should be sent")
                            }
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
    private fun getTravelTimeAndCheckLeaveReminder(
        context: Context,
        eventStartTime: String,
        eventLocation: String,
        reminderBufferMinutes: Int,
        eventTitle: String,
        eventStartDate: String
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d("ReminderReceiver", "Location permission not granted.")
            return
        }

        //fusedLocationClient.lastLocation.addOnSuccessListener { currentLocation ->
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { currentLocation ->
            if (currentLocation == null) {
                Log.d("ReminderReceiver", "Current location unavailable.")
                return@addOnSuccessListener
            }

            val geocoder = Geocoder(context, Locale.getDefault())
            val destinationList = geocoder.getFromLocationName(eventLocation, 1)
            if (destinationList.isNullOrEmpty()) {
                Log.d("ReminderReceiver", "Could not geocode destination: $eventLocation")
                return@addOnSuccessListener
            }

            val destination = Location("").apply {
                latitude = destinationList[0].latitude
                longitude = destinationList[0].longitude
            }

            val travelDistance = currentLocation.distanceTo(destination)
            val averageSpeed = 15  // m/s
            var travelTimeMillis = (travelDistance / averageSpeed * 1000).toLong()
//            val travelTimeMin = travelTimeMillis / 1000 / 60

//            if (travelTimeMin > 10) {  // 10 minute travel cap for testing It was giving me like 50 hours to drive to concordia
//                Log.d("TravelTime", "Travel time in minutes is: $travelTimeMin")
//                Log.d("TravelTime", "Travel time seems too large, capping to 10 minutes")
//                travelTimeMillis = 10L * 60L * 1000L
//            }

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val eventDateAndTime = format.parse("$eventStartDate $eventStartTime") ?: return@addOnSuccessListener
            val eventTime = Calendar.getInstance().apply {
                time = eventDateAndTime
            }

            val leaveTimeMillis = eventTime.timeInMillis - travelTimeMillis - (reminderBufferMinutes * 60 * 1000)
            val currentTime = System.currentTimeMillis()

            Log.d("ReminderReceiver", "currentTime: ${Date(currentTime)}")
            Log.d("ReminderReceiver", "eventTimeMillis: ${eventTime.timeInMillis} (${Date(eventTime.timeInMillis)})")
            Log.d("ReminderReceiver", "leaveTimeMillis: ${leaveTimeMillis} (${Date(leaveTimeMillis)})")
            Log.d("ReminderReceiver", "travelTimeMillis: ${travelTimeMillis} (${travelTimeMillis / 1000 / 60} minutes)")

            if (currentTime in leaveTimeMillis until (leaveTimeMillis + 60000)) {
                Log.d("NotificationSending", "Sending location-based notification for: $eventTitle")
                sendLeaveNowNotification(context, eventLocation, eventTitle)
            } else {
                Log.d("WrongTime", "Not time yet for location-based: $eventTitle")
            }
        }

    }
}
fun sendLeaveNowNotification(context: Context, eventLocation: String, eventTitle: String) {
    val notification = NotificationCompat.Builder(context, "event_reminders")
        .setContentTitle("Time to leave")
        .setContentText("Your event $eventTitle at $eventLocation is starting soon.")
        .setSmallIcon(R.drawable.notification)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(System.currentTimeMillis().toInt(), notification)
}





