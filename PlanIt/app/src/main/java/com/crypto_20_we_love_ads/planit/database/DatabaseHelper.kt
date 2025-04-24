package com.crypto_20_we_love_ads.planit.database

import android.content.Context
import android.content.ContentValues

import android.database.Cursor

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CALENDAR)
        // Insert predefined row when the table is created
        insertPredefinedEventIfNotExists(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALENDAR")
        onCreate(db)
    }

    // Insert predefined event into the database only if it doesn't already exist
    private fun insertPredefinedEventIfNotExists(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CALENDAR WHERE title = ?", arrayOf("Test1"))

        if (cursor.count == 0) {  // Only insert if no event with the title "Test1" exists
            val values = ContentValues().apply {
                put("title", "Test1")  // Original title
                put("category", "Sports")  // Original category
                put("startDate", "2025-04-16")  // Original start date
                put("endDate", "2025-04-10")  // Original end date
                put("startTime", "02:45")  // Original event time
                put("endTime", "03:45")  // Original end time
                put("description", "blah")  // Original description
                put("dayOfWeek", "M")  // Original day of the week
                put("reminder1", "None")  // Original reminder (true)
                put("reminder2", "")
                put("importance", 3)  // Original importance level (e.g., 3)
                put("recurring", 1)  // Original recurring value (true)
                put("location", "IDK")  // Original location
            }
            db.insert(TABLE_CALENDAR, null, values)
        }
        cursor.close()
    }

    //Find events by their ID. Used for the edit and delete functions
    fun getEventById(id: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_CALENDAR WHERE id = ?", arrayOf(id.toString()))
    }

    /*
    Edit events already in the database
     */

    fun editEvent(id: Int, title: String, category: String, startDate: String, endDate: String,
                  startTime: String, endTime: String, dayOfWeek: String, reminder1: String, reminder2: String,
                  importance: Int, recurring: Boolean, location: String, description: String): Boolean{

        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("category", category)
            put("startDate", startDate)
            put("endDate", endDate)
            put("startTime", startTime)
            put("endTime", endTime)
            put("dayOfWeek", dayOfWeek)
            put("reminder1", reminder1) //Decided to store reminders as strings if (reminder) 1 else 0) // 1 for true, 0 for false
            put("reminder2", reminder2)
            put("importance", importance)
            put("recurring", if (recurring) 1 else 0) // 1 for true, 0 for false
            put("location", location)
            put("description", description)
        }
        val selection = "id = ?"
        val selectionArgs = arrayOf(id.toString())

        val rowsUpdated = db.update("Calendar", values, selection, selectionArgs)

        return rowsUpdated > 0
    }

    /*
    DELETE EVENTS
     */
    fun deleteEvent(id: Int): Boolean{
        val db = this.writableDatabase
        val rowsDeleted = db.delete("Calendar", "id = ?", arrayOf(id.toString()))
        return rowsDeleted > 0
    }

    // Insert new event into the database
    fun insertEvent(
        title: String,
        category: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
        dayOfWeek: String,
        reminder1: String,
        reminder2: String,
        importance: Int,
        recurring: Boolean,
        recurringEnd: String,
        location: String,
        description: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("category", category)
            put("startDate", startDate)
            put("endDate", endDate)
            put("startTime", startTime)
            put("endTime", endTime)
            put("dayOfWeek", dayOfWeek)
            put("reminder1", reminder1) //Decided to store reminders as strings if (reminder) 1 else 0) // 1 for true, 0 for false
            put("reminder2", reminder2)
            put("importance", importance)
            put("recurring", if (recurring) 1 else 0) // 1 for true, 0 for false
            put("recurringEnd", recurringEnd)
            put("location", location)
            put("description", description)
        }
        db.insert(TABLE_CALENDAR, null, values)
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "events.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_CALENDAR = "Calendar"

        // Table Creation SQL with DATE type for startDate and endDate
        private const val CREATE_TABLE_CALENDAR = """
            CREATE TABLE $TABLE_CALENDAR (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                category TEXT,
                startDate DATE,
                endDate DATE,
                startTime TEXT,
                endTime TEXT,
                dayOfWeek TEXT,
                /*Changed from integer to TEXT based upon the selection method and added the second reminder for location based reminders*/
                reminder1 TEXT,
                reminder2 TEXT,
                importance INTEGER,
                recurring INTEGER,
                recurringEnd DATE,
                location TEXT,
                description TEXT
            );
        """
    }
}
