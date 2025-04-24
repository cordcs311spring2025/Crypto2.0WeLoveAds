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

class AddActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_screen)

        /*
        DATE and Time PICKERS
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
        val catOptions = arrayOf("None", "Home", "Work", "Sport", "School", "Birthday", "Social", "Event")
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
            impSel.showDropDown()

        }

        //Reminder dropdown menu
        val remOptions = arrayOf("None", "Location Based", "5 Minutes", "10 Minutes", "15 Minutes", "20 Minutes", "30 Minutes", "45 Minutes", "1 Hour")
        val remSelected = findViewById<AutoCompleteTextView>(R.id.autoTVRem)
        val remAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, remOptions)
        remSelected.setAdapter(remAdapter)
        remSelected.setOnClickListener {
            remSelected.showDropDown()
        }

        //Second reminder Options if user selects location based
        val remOptions2 = arrayOf("5 Minutes", "10 Minutes", "15 Minutes", "20 Minutes", "30 Minutes", "45 Minutes", "1 Hour")
        val remSelected2 = findViewById<AutoCompleteTextView>(R.id.autoTVRem2)
        val remAdapter2 = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, remOptions2)
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
            }
            else {
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


            // Add Event button redirection (Assuming there's an AddEventActivity)
            findViewById<View>(R.id.addEvent).setOnClickListener {
                val intent = Intent(this, AddActivity::class.java) // Change to correct activity
                startActivity(intent)
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

            // Back Button functionality
            findViewById<View>(R.id.backButton).setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }


        //Database stuff here
        val createSubmit = findViewById<Button>(R.id.createButton)

        createSubmit.setOnClickListener {
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
            //Ensures a default value if none selected
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
            val recurringEnd = findViewById<EditText>(R.id.editTextRecEndDate).text.toString()
            val dayOfWeek = findViewById<Button>(R.id.btnDaySel).text.toString()
            val location = findViewById<EditText>(R.id.editTextLocation).text.toString()
            val description = findViewById<EditText>(R.id.editTextBox).text.toString()

            //Date validation
            val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
            if (!dateRegex.matches(startDate) || !dateRegex.matches(endDate)) {
                Toast.makeText(this, "Please enter dates in YYYY-MM-DD format", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val timeRegex = Regex("""\d{2}:\d{2}""")
            if (!timeRegex.matches(startTime) || !timeRegex.matches(endTime)) {
                Toast.makeText(this, "Please enter times in HH:mm format", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //adding info to the database
            val dbHelper = DatabaseHelper(this)

            dbHelper.insertEvent(
                title,
                category,
                startDate,
                endDate,
                startTime,
                endTime,
                dayOfWeek,
                reminder1,
                reminder2,
                importance,
                recurring,
                recurringEnd,
                location,
                description,
            )
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
