package com.crypto_20_we_love_ads.planit

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var occassionText: TextView
    private lateinit var occassionDOW: TextView
    private lateinit var occassionTime: TextView
    private lateinit var occassionImportance: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var eventListLayout: LinearLayout
    private val eventDates = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calendar_screen)

        // Initialize views
        calendarView = findViewById(R.id.calendarView)
        occassionText = findViewById(R.id.occassionText)
        occassionDOW = findViewById(R.id.occassionDOW)
        occassionTime = findViewById(R.id.occassionTime)
        occassionImportance = findViewById(R.id.important)
        eventListLayout = findViewById(R.id.eventList)
        dbHelper = DatabaseHelper(this)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            val dayOfWeek = getDayOfWeek(year, month, dayOfMonth)
            displayEvents(selectedDate, dayOfWeek)
        }

        // Load event dates to decorate the calendar with circles
        loadEventDates()

        // Navigation buttons
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

        // Handle clicking the TextView to redirect to MainActivity with the selected date
        findViewById<TextView>(R.id.textView).setOnClickListener {
            val selectedDateMillis = calendarView.date // Long value representing milliseconds
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDateMillis // Set the calendar to the selected date in milliseconds

            val selectedYear = calendar.get(Calendar.YEAR)
            val selectedMonth = calendar.get(Calendar.MONTH) // Month is zero-based (0-11)
            val selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

            val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("selectedDate", selectedDate) // Pass the selected date
            startActivity(intent)
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

    private fun convertImportance(value: Int): String {
        return when (value) {
            1 -> "!"
            2 -> "!!"
            3 -> "!!!"
            else -> ""
        }
    }

    private fun loadEventDates() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT startDate FROM Calendar", null
        )

        while (cursor.moveToNext()) {
            val eventDate = cursor.getString(0)
            eventDates.add(eventDate)
        }

        cursor.close()
    }

    private fun displayEvents(date: String, dayOfWeek: String) {
        eventListLayout.removeAllViews()

        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT title, startTime, importance FROM Calendar WHERE startDate = ?",
            arrayOf(date)
        )

        Log.d("CalendarDebug", "Query for date $date returned ${cursor.count} events")

        val eventTitles = mutableListOf<String>()
        val eventTimes = mutableListOf<String>()
        val eventImportances = mutableListOf<String>()

        while (cursor.moveToNext()) {
            eventTitles.add(cursor.getString(0))
            eventTimes.add(cursor.getString(1))
            eventImportances.add(convertImportance(cursor.getInt(2)))
        }

        cursor.close()

        if (eventTitles.isNotEmpty()) {
            occassionDOW.text = dayOfWeek

            // Set a circle around dates with events
            highlightEventDates()

            for (i in eventTitles.indices) {
                val eventContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(12, 12, 12, 12)
                    gravity = Gravity.CENTER_VERTICAL
                    elevation = 4f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 12)
                    }
                }

                val importantTextView = TextView(this).apply {
                    text = eventImportances[i]
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 30
                    }
                }

                val occasionTextView = TextView(this).apply {
                    text = eventTitles[i]
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 30
                    }
                }

                val occasionDOWTextView = TextView(this).apply {
                    text = dayOfWeek
                    setTextColor(ContextCompat.getColor(context, R.color.teal))
                    textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 30
                    }
                }

                val occasionTimeTextView = TextView(this).apply {
                    text = eventTimes[i]
                    textSize = 18f
                }

                // Add views to the container
                eventContainer.addView(importantTextView)
                eventContainer.addView(occasionTextView)
                eventContainer.addView(occasionDOWTextView)
                eventContainer.addView(occasionTimeTextView)

                // Add the container to the layout
                eventListLayout.addView(eventContainer)
            }

        } else {
            occassionText.text = "No events on this date"
            occassionTime.text = "--:--"
            occassionImportance.text = "Importance: N/A"
        }
    }

    private fun highlightEventDates() {
        // Highlight dates with events using the CalendarView
        for (date in eventDates) {
            val calendar = Calendar.getInstance()
            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            parsedDate?.let { calendar.time = it }
            //calendarView.setDate(calendar.timeInMillis)
        }
    }
}
