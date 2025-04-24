package com.crypto_20_we_love_ads.planit

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var currentDate: TextView
    private lateinit var currentDOW: TextView
    private lateinit var eventName: TextView
    private lateinit var currentEventTime: TextView
    private lateinit var currentLocationText: TextView
    private lateinit var dbHelper: DatabaseHelper

    private var currentCalendar: Calendar = Calendar.getInstance()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                val permission = entry.key
                val granted = entry.value
                val message = if (granted) "$permission granted" else "$permission denied"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Create the notification channel if not already created
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            if (manager.getNotificationChannel("event_reminders") == null) {
                val channel = NotificationChannel(
                    "event_reminders",
                    "Event Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager.createNotificationChannel(channel)
            }
        }

        // Other setup code
        currentDate = findViewById(R.id.currentDate)
        currentDOW = findViewById(R.id.currentDOW)
        eventName = findViewById(R.id.currentEventName)
        currentEventTime = findViewById(R.id.currentEventTime)
        currentLocationText = findViewById(R.id.currentLocationText)
        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestLocationAndNotificationPermissions()


        // Set the repeating alarm to check for notifications
        val reminderReceiver = ReminderReceiver()
        reminderReceiver.setRepeatingAlarm(this)  // Set up alarm for recurring reminder check


        findViewById<View>(R.id.addEvent).setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        findViewById<View>(R.id.homeButton).setOnClickListener {
            // Stay on main screen
        }

        findViewById<View>(R.id.scheduleButton).setOnClickListener {
            startActivity(Intent(this, CalenderActivity::class.java))
        }

        findViewById<View>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<View>(R.id.currentEventName).setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

        findViewById<View>(R.id.prevDay).setOnClickListener {
            changeDateByDay(-1)
        }

        findViewById<View>(R.id.nextDay).setOnClickListener {
            changeDateByDay(1)
        }

        findViewById<View>(R.id.currentEventLocation).setOnClickListener {
            openLocationInMaps()
        }

        // Handle the selected date passed from CalenderActivity
        val selectedDate = intent.getStringExtra("selectedDate")
        if (selectedDate != null) {
            val calendar = Calendar.getInstance()
            val dateParts = selectedDate.split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1
            val day = dateParts[2].toInt()
            calendar.set(year, month, day)
            currentCalendar = calendar
            displayEventsForCurrentDate()
        } else {
            displayEventsForCurrentDate()
        }
    }


    private fun requestLocationAndNotificationPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun openLocationInMaps() {
        val location = currentLocationText.text.toString()
        if (location.isNotEmpty()) {
            val geoUri = "geo:0,0?q=$location"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return SimpleDateFormat("MMMM d", Locale.getDefault()).format(calendar.time)
    }

    private fun getDayOfWeek(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
    }

    private fun changeDateByDay(dayChange: Int) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, dayChange)
        displayEventsForCurrentDate()
    }

    private fun displayEventsForCurrentDate() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Calendar.getInstance().apply {
                set(year, month, day)
            }.time
        )
        val dayOfWeek = getDayOfWeek(year, month, day)

        displayEvents(dateFormatted, dayOfWeek)
    }

    private fun displayEvents(date: String, dayOfWeek: String) {
        val db = dbHelper.readableDatabase
//        val cursor = db.rawQuery("SELECT * FROM calendar WHERE startDate = ?", arrayOf(date))
        val cursor = db.rawQuery(
            """
    SELECT * FROM calendar 
    WHERE startDate = ? 
       OR (
        dayOfWeek IS NOT NULL 
        AND dayOfWeek LIKE '%' || ? || '%'
        AND (recurringEnd IS NULL OR recurringEnd >= ?))
    """.trimIndent(), arrayOf(date, dayOfWeek, date)
        )


        val eventListLayout = findViewById<LinearLayout>(R.id.eventList)
        eventListLayout.removeViews(1, eventListLayout.childCount - 1)

        val formattedDate = SimpleDateFormat("MMMM d", Locale.getDefault()).format(currentCalendar.time)
        currentDate.text = formattedDate

        currentDOW.text = dayOfWeek

        if (cursor.moveToFirst()) {
            do {

                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id")) // <- NEW LINE

                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val eventTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"))
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val importance = cursor.getInt(cursor.getColumnIndexOrThrow("importance"))

                val clone = LayoutInflater.from(this).inflate(R.layout.event_item, eventListLayout, false)
                clone.visibility = View.VISIBLE



                clone.findViewById<TextView>(R.id.currentEventName).apply {
                    text = title
                    setOnClickListener {
                        val intent = Intent(this@MainActivity, EditActivity::class.java).apply {

                            putExtra("id", eventId) // <- NEW LINE
                            /*
                            I'm just going to query the db based on the event ID
                            putExtra("title", title)
                            putExtra("startTime", eventTime)
                            putExtra("location", location)
                            putExtra("date", date)

                  */

                        }
                        startActivity(intent)
                    }
                }

                clone.findViewById<TextView>(R.id.currentEventTime).text = eventTime
                clone.findViewById<TextView>(R.id.currentLocationText).text = location

                val importanceView = clone.findViewById<TextView>(R.id.important)
                val importanceText = when (importance) {
                    1 -> "!"
                    2 -> "!!"
                    3 -> "!!!"
                    else -> ""
                }
                importanceView.text = importanceText
                importanceView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

                clone.findViewById<View>(R.id.currentEventLocation).setOnClickListener {
                    val geoUri = "geo:0,0?q=$location"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                }

                eventListLayout.addView(clone)

            } while (cursor.moveToNext())
        } else {
            val noEventText = TextView(this).apply {
                text = "No events today"
                textSize = 20f
                setPadding(16, 16, 16, 16)
            }
            eventListLayout.addView(noEventText)
        }

        cursor.close()
    }
}
