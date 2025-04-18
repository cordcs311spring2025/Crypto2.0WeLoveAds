package com.crypto_20_we_love_ads.planit

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Gravity
import android.widget.CalendarView
import android.widget.TextView
import android.widget.LinearLayout
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class CalenderActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var occassionText: TextView
    private lateinit var occassionDOW: TextView
    private lateinit var occassionTime: TextView
    private lateinit var occassionImportance: TextView  // New TextView for Importance
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var eventListLayout: LinearLayout  // Layout to hold event views dynamically

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calender_screen)

        calendarView = findViewById(R.id.calendarView)
        occassionText = findViewById(R.id.occassionText)
        occassionDOW = findViewById(R.id.occassionDOW)
        occassionTime = findViewById(R.id.occassionTime)
        occassionImportance = findViewById(R.id.important)  // Initialize Importance TextView
        eventListLayout = findViewById(R.id.eventList)  // Layout for dynamic event views

        dbHelper = DatabaseHelper(this)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            val dayOfWeek = getDayOfWeek(year, month, dayOfMonth)
            /*
            Hardcoding the date for now make sure to change back
             */
            displayEvents("2025-04-18", dayOfWeek)
        }

        findViewById<View>(R.id.addEvent).setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        findViewById<View>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<View>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return String.format("%04d-%02d-%02d", year, month + 1, day)
    }

    private fun getDayOfWeek(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply { set(year, month, day) }
        val days = arrayOf("Sun", "M", "T", "W", "Th", "F", "Sat")
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }

    private fun displayEvents(date: String, dayOfWeek: String) {
        // Clear any existing event views
        eventListLayout.removeAllViews()

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT title, startTime, importance FROM Calendar WHERE startDate = ?", arrayOf(date))

        val eventTitles = mutableListOf<String>()
        val eventTimes = mutableListOf<String>()
        val eventImportances = mutableListOf<String>()  // List to hold importance values

        while (cursor.moveToNext()) {
            eventTitles.add(cursor.getString(0))
            eventTimes.add(cursor.getString(1))
            eventImportances.add(cursor.getInt(2).toString())  // Get the importance value (converted to String)
        }

        cursor.close()

        if (eventTitles.isNotEmpty()) {
            // Display day of week
            occassionDOW.text = dayOfWeek

            // Iterate over the events and create a new LinearLayout for each one
            for (i in eventTitles.indices) {
                // Create a new LinearLayout dynamically to hold each event
                val eventContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(12, 12, 12, 12)
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 12) // Margin between event containers
                    }
                }

                // Create the TextViews dynamically and add them to the LinearLayout

                // Importance TextView
                val importantTextView = TextView(this).apply {
                    text = eventImportances[i]
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 12
                    }
                }

                // Event Title TextView
                val occasionTextView = TextView(this).apply {
                    text = eventTitles[i]
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 12
                    }
                }

                // Day of Week TextView
                val occasionDOWTextView = TextView(this).apply {
                    text = dayOfWeek
                    setTextColor(ContextCompat.getColor(context, R.color.teal))
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 12
                    }
                }

                // Event Time TextView
                val occasionTimeTextView = TextView(this).apply {
                    text = eventTimes[i]
                    textSize = 18f
                }

                // Add TextViews to the eventContainer
                eventContainer.addView(importantTextView)
                eventContainer.addView(occasionTextView)
                eventContainer.addView(occasionDOWTextView)
                eventContainer.addView(occasionTimeTextView)

                // Add the eventContainer to the eventList layout
                eventListLayout.addView(eventContainer)
            }
        } else {
            occassionText.text = "No event"
            occassionTime.text = "--:--"
            occassionImportance.text = "Importance: N/A"  // Default importance value when no events
        }
    }
}
