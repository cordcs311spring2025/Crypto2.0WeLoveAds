package com.crypto_20_we_love_ads.planit

import android.annotation.SuppressLint
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

class AddActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_screen)


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
        switchRec.setOnCheckedChangeListener { _, isSwitched ->
            if (isSwitched) {
                dayDDmenu.visibility = View.VISIBLE
            } else {
                dayDDmenu.visibility = View.GONE
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
            val category = findViewById<AutoCompleteTextView>(R.id.autoTVCat).text.toString()
            val startDate = findViewById<EditText>(R.id.editTextDate).text.toString()
            val startTime = findViewById<EditText>(R.id.editTextTime).text.toString()
            val endDate = findViewById<EditText>(R.id.editTextDateEnd).text.toString()
            val endTime = findViewById<EditText>(R.id.editTextTimeEnd).text.toString()
            val reminder1 = findViewById<AutoCompleteTextView>(R.id.autoTVRem).text.toString()
            val reminder2 = findViewById<AutoCompleteTextView>(R.id.autoTVRem2).text.toString()
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

            //adding info to the database
            val dbHelper = DatabaseHelper(this)
            dbHelper.insertEvent(
                title,
                category,
                startDate,
                endDate,
                startTime,
                endTime,
                description,
                dayOfWeek,
                reminder1,
                reminder2,
                importance,
                recurring,
                location,
            )
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }


    }
}
