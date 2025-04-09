package com.crypto_20_we_love_ads.planit

import android.content.Intent
import android.net.Uri  // Import Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast  // Import Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var eventContainer: View


    private var currentCalendar: Calendar = Calendar.getInstance() // To keep track of the current date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        currentDate = findViewById(R.id.currentDate)
        eventContainer = findViewById(R.id.eventContainer)
        currentDOW = findViewById(R.id.currentDOW)
        eventName = findViewById(R.id.currentEventName)  // TextView for event name
        currentEventTime = findViewById(R.id.currentEventTime)  // TextView for event time
        currentLocationText = findViewById(R.id.currentLocationText)  // TextView for location
        dbHelper = DatabaseHelper(this)

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add Event button redirection
        findViewById<View>(R.id.addEvent).setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }



        // Home Button stays on the same page (no action needed)
        findViewById<View>(R.id.homeButton).setOnClickListener {
            // No action, stays on this page
        }

        // Schedule Button redirection
        findViewById<View>(R.id.scheduleButton).setOnClickListener {
            val intent = Intent(this, CalenderActivity::class.java)
            startActivity(intent)
        }

        // Search Button redirection
        findViewById<View>(R.id.searchButton).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Settings Button redirection
        findViewById<View>(R.id.settingsButton).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.currentEventName).setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }


        // Navigation buttons for previous and next day
        findViewById<View>(R.id.prevDay).setOnClickListener {
            changeDateByDay(-1)
        }
        findViewById<View>(R.id.nextDay).setOnClickListener {
            changeDateByDay(1)
        }

        // Location ImageView click listener
        findViewById<View>(R.id.currentEventLocation).setOnClickListener {
            openLocationInMaps() // Call openLocationInMaps when the ImageView is clicked
        }

        // Call method to display events for today
        displayEventsForCurrentDate()
    }

    // Function to open the location in Google Maps
    private fun openLocationInMaps() {
        val location = currentLocationText.text.toString()
        if (location.isNotEmpty()) {
            val geoUri = "geo:0,0?q=$location"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))  // Use Uri.parse() for geo URI
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } else {
            // Handle case where location is empty
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()  // Display a toast message
        }
    }

    // Function to format the selected date (Month Day, e.g., "April 2")
    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault()) // Example: "April 2"
        return dateFormat.format(calendar.time)
    }

    // Function to get the full name of the day of the week (e.g., Monday, Tuesday)
    private fun getDayOfWeek(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        return dayOfWeek ?: ""  // Default to empty string if null
    }

    // Function to update date by day (next or previous)
    private fun changeDateByDay(dayChange: Int) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, dayChange)
        displayEventsForCurrentDate()
    }

    // Display events for the current date
    private fun displayEventsForCurrentDate() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        // Format date and day of week
        val formattedDate = formatDate(year, month, day)
        val dayOfWeek = getDayOfWeek(year, month, day)

        // Query and display events for the current date
        displayEvents(formattedDate, dayOfWeek)
    }

    // Function to query and display event details for a specific date
    private fun displayEvents(date: String, dayOfWeek: String) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Calendar WHERE startDate = ?", arrayOf(date))

        if (cursor.moveToFirst()) {
            try {
                val titleIndex = cursor.getColumnIndexOrThrow("title")
                val eventTimeIndex = cursor.getColumnIndexOrThrow("eventTime")
                val locationIndex = cursor.getColumnIndexOrThrow("location")

                val title = cursor.getString(titleIndex)
                val eventTime = cursor.getString(eventTimeIndex)
                val location = cursor.getString(locationIndex)

                // Convert the date string from yyyy-MM-dd to a Date object for formatting
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedDate = dateFormat.parse(date)

                // Format the date to "MMMM d" (e.g., "April 8")
                val displayDateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
                val formattedDate = parsedDate?.let { displayDateFormat.format(it) } ?: date

                // Update the UI with the event data
                currentDate.text = formattedDate
                currentDOW.text = dayOfWeek  // Full day of the week name (e.g., Monday)
                eventName.text = title
                currentEventTime.text = eventTime
                currentLocationText.text = location
            } catch (e: IllegalArgumentException) {
                // Handle exception (if any)
            }
        } else {
            // No events found for this date, show default message
            currentDate.text = date
            currentDOW.text = dayOfWeek  // Full day of the week name
            eventName.text = "No events today"
            currentEventTime.text = "--:--"
            currentLocationText.text = "No location"
        }

        cursor.close()
    }
}
