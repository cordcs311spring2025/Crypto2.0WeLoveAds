package com.crypto_20_we_love_ads.planit

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class CalenderActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var occassionText: TextView
    private lateinit var occassionDOW: TextView
    private lateinit var occassionTime: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calender_screen)

        calendarView = findViewById(R.id.calendarView)
        occassionText = findViewById(R.id.occassionText)
        occassionDOW = findViewById(R.id.occassionDOW)
        occassionTime = findViewById(R.id.occassionTime)

        dbHelper = DatabaseHelper(this)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            val dayOfWeek = getDayOfWeek(year, month, dayOfMonth)
            displayEvents(selectedDate, dayOfWeek)
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
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT title, eventTime FROM Calendar WHERE startDate = ?", arrayOf(date))

        val eventTitles = mutableListOf<String>()
        val eventTimes = mutableListOf<String>()

        while (cursor.moveToNext()) {
            eventTitles.add(cursor.getString(0))
            eventTimes.add(cursor.getString(1))
        }

        cursor.close()

        if (eventTitles.isNotEmpty()) {
            occassionText.text = eventTitles.joinToString("\n")
            occassionDOW.text = dayOfWeek
            occassionTime.text = eventTimes.joinToString("\n")
        } else {
            occassionText.text = "No event"
            occassionDOW.text = dayOfWeek
            occassionTime.text = "--:--"
        }
    }
}
