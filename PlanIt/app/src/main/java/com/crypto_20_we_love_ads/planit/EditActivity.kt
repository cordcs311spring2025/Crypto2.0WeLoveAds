package com.crypto_20_we_love_ads.planit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_screen)


        /*
        DATE and Time PICKER
         */
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val editTextDateEnd = findViewById<EditText>(R.id.editTextDateEnd)
        val editTextTime = findViewById<EditText>(R.id.editTextTime)
        val editTextTimeEnd = findViewById<EditText>(R.id.editTextTimeEnd)
        val editTextRecDateEnd = findViewById<EditText>(R.id.editTextRecEndDate)

        editTextDate.setOnClickListener { showDatePicker(editTextDate) }
        editTextDateEnd.setOnClickListener { showDatePicker(editTextDateEnd) }
        editTextTime.setOnClickListener { showTimePicker(editTextTime) }
        editTextTimeEnd.setOnClickListener { showTimePicker(editTextTimeEnd) }
        editTextRecDateEnd.setOnClickListener { showDatePicker(editTextRecDateEnd)}

        // Category drop down
        val catOptions =
            arrayOf("None", "Home", "Work", "Sport", "School", "Birthday", "Social", "Event")
        val catSel = findViewById<AutoCompleteTextView>(R.id.autoTVCat)
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, catOptions)
        catSel.setAdapter(catAdapter)
        catSel.setOnClickListener {
            catSel.showDropDown()
        }


        // Importance drop down menu
        val impOptions = arrayOf("Low", "Medium", "High")
        val impSel = findViewById<AutoCompleteTextView>(R.id.autoTVImport)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, impOptions)
        impSel.setAdapter(adapter)
        impSel.setOnClickListener {
        //impSel.setOnFocusChangeListener { _, hasFocus ->
            impSel.showDropDown()
        }

        //Reminder dropdown menu
        val remOptions = arrayOf(
            "None",
            "Location Based",
            "5 Minutes",
            "10 Minutes",
            "15 Minutes",
            "20 Minutes",
            "30 Minutes",
            "45 Minutes",
            "1 Hour"
        )
        val remSelected = findViewById<AutoCompleteTextView>(R.id.autoTVRem)
        val remAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, remOptions)
        remSelected.setAdapter(remAdapter)
        remSelected.setOnClickListener {
            remSelected.showDropDown()
        }

        //Second reminder Options if user selects location based
        val remOptions2 = arrayOf(
            "5 Minutes",
            "10 Minutes",
            "15 Minutes",
            "20 Minutes",
            "30 Minutes",
            "45 Minutes",
            "1 Hour"
        )
        val remSelected2 = findViewById<AutoCompleteTextView>(R.id.autoTVRem2)
        val remAdapter2 =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, remOptions2)
        remSelected2.setAdapter(remAdapter2)
        remSelected2.setOnClickListener {
            remSelected2.showDropDown()
        }

        //Turns the second dropdown if Location based is true

        remSelected.setOnItemClickListener { _, _, num, _ ->
            val selectedDDOption = remOptions[num]
            val remMenu2 = findViewById<TextInputLayout>(R.id.remMenu2)
            if (selectedDDOption == "Location Based") {
                remMenu2.visibility = View.VISIBLE
            } else {
                remMenu2.visibility = View.GONE
            }
        }

        //Recurring events switch actions
        //Visability
        val switchRec = findViewById<Switch>(R.id.switchRec)
        val dayDDmenu = findViewById<Button>(R.id.btnDaySel)
        val recEndDate = findViewById<EditText>(R.id.editTextRecEndDate)
        switchRec.setOnCheckedChangeListener { _, isSwitched ->
            if (isSwitched) {
                dayDDmenu.visibility = View.VISIBLE
                recEndDate.visibility = View.VISIBLE
            } else {
                dayDDmenu.visibility = View.GONE
                recEndDate.visibility = View.GONE
            }
        }


        //Day selector for recurring events
        val dayOptions =
            arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val daysSelected = mutableListOf<String>()
        val daysChecked = BooleanArray(dayOptions.size)
        val btnDaySel = findViewById<Button>(R.id.btnDaySel)

        btnDaySel.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Options")
                .setMultiChoiceItems(dayOptions, daysChecked) { _, index, isChecked ->
                    if (isChecked) {
                        daysSelected.add(dayOptions[index])
                    } else {
                        daysSelected.remove(dayOptions[index])
                    }
                }
                .setPositiveButton("OK") { _, _ ->
                    btnDaySel.text = "${daysSelected.toString()}"
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

            /*
            EDIT EVENT button
             */
            findViewById<Button>(R.id.editButton).setOnClickListener {
                //Gathering all the values from the screen
                val title = findViewById<EditText>(R.id.editTextTitle).text.toString()
                if (title == "") {
                    Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                var category = findViewById<AutoCompleteTextView>(R.id.autoTVCat).text.toString()
                if (category == "") {
                    category = "none"
                }
                val startDate = findViewById<EditText>(R.id.editTextDate).text.toString()
                val startTime = findViewById<EditText>(R.id.editTextTime).text.toString()
                val endDate = findViewById<EditText>(R.id.editTextDateEnd).text.toString()
                val endTime = findViewById<EditText>(R.id.editTextTimeEnd).text.toString()
                val reminder1 = findViewById<AutoCompleteTextView>(R.id.autoTVRem).text.toString()
                var reminder2 = findViewById<AutoCompleteTextView>(R.id.autoTVRem2).text.toString()
                if (reminder1 != "Location Based"){
                    reminder2 = ""
                }
                if (reminder1 == "Location Based" && reminder2 == ""){
                    reminder2 = "15 Minutes"
                }
                val importanceText = findViewById<AutoCompleteTextView>(R.id.autoTVImport).text.toString()
                //mapping the strings to numbers
                val importance = when (importanceText) {
                    "Low" -> 1
                    "Medium" -> 2
                    "High" -> 3
                    else -> 1 //Defaults to low
                }
                val recurring = findViewById<Switch>(R.id.switchRec).isChecked
                val dayOfWeek = findViewById<Button>(R.id.btnDaySel).text.toString()
                val location = findViewById<EditText>(R.id.editTextLocation).text.toString()
                val description = findViewById<EditText>(R.id.editTextBox).text.toString()

                //Date and Time validation. These aren't really needed anymore but just in case ig
                val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
                if (!dateRegex.matches(startDate) || !dateRegex.matches(endDate)) {
                    Toast.makeText(this, "Please enter dates in YYYY-MM-DD format", Toast.LENGTH_SHORT)
                        .show()
                }
                val timeRegex = Regex("""\d{2}:\d{2}""")
                if (!timeRegex.matches(startTime) || !timeRegex.matches(endTime)) {
                    Toast.makeText(this, "Please enter times in HH:mm format", Toast.LENGTH_SHORT)
                        .show()
                }
                val eventID = intent.getIntExtra("id", -1)
                if (eventID != -1) {
                    val dbHelper = DatabaseHelper(this)
                    val isEdited = dbHelper.editEvent(eventID, title, category, startDate, endDate, startTime, endTime,
                        dayOfWeek, reminder1, reminder2, importance, recurring, location, description)

                    if (isEdited) {
                        Toast.makeText(this, "event successfully updated", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java) // Update if needed
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Event not updated. Try again", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show()
                }

            }
            /*
            Deleting events
             */
            findViewById<Button>(R.id.deleteButton).setOnClickListener {
                val eventID = intent.getIntExtra("id", -1)
                if (eventID != -1) {
                    val dbHelper = DatabaseHelper(this)
                    val isDeleted = dbHelper.deleteEvent(eventID)

                    if (isDeleted) {
                        Toast.makeText(this, "Event Deleted", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
                }
                    }


            // Home Button redirection
            findViewById<View>(R.id.homeButton).setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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

            // Back Button functionality (Fixed missing findViewById)
            findViewById<View>(R.id.backButton).setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        /*
        Prepopulating the event information
         */
        val eventID = intent.getIntExtra("id", -1)
        if (eventID != -1) {
            val dbHelper = DatabaseHelper(this)
            val cursor = dbHelper.getEventById(eventID)
            cursor?.use {
                if (it.moveToFirst()) {
                    //Gathering variables
                    val title = it.getString(it.getColumnIndexOrThrow("title"))
                    val category = it.getString(it.getColumnIndexOrThrow("category"))
                    val startDate = it.getString(it.getColumnIndexOrThrow("startDate"))
                    val endDate = it.getString(it.getColumnIndexOrThrow("endDate"))
                    val startTime = it.getString(it.getColumnIndexOrThrow("startTime"))
                    val endTime = it.getString(it.getColumnIndexOrThrow("endTime"))
                    val dayOfWeek = it.getString(it.getColumnIndexOrThrow("dayOfWeek"))
                    val reminder1 = it.getString(it.getColumnIndexOrThrow("reminder1"))
                    val reminder2 = it.getString(it.getColumnIndexOrThrow("reminder2"))
                    val importance = it.getInt(it.getColumnIndexOrThrow("importance"))
                    //mapping the numbers back to the strings
                    val importText = when (importance) {
                        1 -> "Low"
                        2 -> "Medium"
                        3 -> "High"
                        else -> "Low"
                    }
                    val recurring = it.getInt(it.getColumnIndexOrThrow("recurring"))
                    val recurringEnd = it.getString(it.getColumnIndexOrThrow("recurringEnd"))
                    val location = it.getString(it.getColumnIndexOrThrow("location"))
                    val description = it.getString(it.getColumnIndexOrThrow("description"))

                    //Setting fields
                    findViewById<EditText>(R.id.editTextTitle).setText(title)
                    findViewById<AutoCompleteTextView>(R.id.autoTVCat).setText(category, false)
                    findViewById<EditText>(R.id.editTextTitle).setText(title)
                    findViewById<EditText>(R.id.editTextDate).setText(startDate)
                    findViewById<EditText>(R.id.editTextTime).setText(startTime)
                    findViewById<EditText>(R.id.editTextDateEnd).setText(endDate)
                    findViewById<EditText>(R.id.editTextTimeEnd).setText(endTime)
                    findViewById<AutoCompleteTextView>(R.id.autoTVRem).setText(reminder1, false)
                    val remMenu2 = findViewById<TextInputLayout>(R.id.remMenu2)
                    if ( remSelected.text.toString() == "Location Based") {
                        remMenu2.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this, remSelected.text.toString(), Toast.LENGTH_SHORT).show()
                        remMenu2.visibility = View.GONE
                    }
                    findViewById<AutoCompleteTextView>(R.id.autoTVRem2).setText(reminder2, false)
                    findViewById<AutoCompleteTextView>(R.id.autoTVImport).setText(importText, false)
                    //findViewById<AutoCompleteTextView>(R.id.autoTVImport).showDropDown()
                    if (recurring == 1) {
                        findViewById<Switch>(R.id.switchRec).isChecked = true
                    }
                    else {
                        findViewById<Switch>(R.id.switchRec).isChecked = false
                    }
                    findViewById<EditText>(R.id.editTextRecEndDate).setText(recurringEnd)
                    findViewById<EditText>(R.id.editTextLocation).setText(location)
                    findViewById<EditText>(R.id.editTextBox).setText(description)
                    findViewById<Button>(R.id.btnDaySel).setText(dayOfWeek)
                }
            }
        }
        else {
                Toast.makeText(this, "Event id mustve been -1", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                editText.setText(formatter.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                editText.setText(formatter.format(selectedTime.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }
}

