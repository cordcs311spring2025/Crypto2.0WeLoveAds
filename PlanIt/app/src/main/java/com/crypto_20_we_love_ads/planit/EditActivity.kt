package com.crypto_20_we_love_ads.planit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class EditActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_screen)

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

            // Add Event button redirection (Change to the actual event-adding activity)
            findViewById<View>(R.id.editEvent).setOnClickListener {
                val intent = Intent(this, AddActivity::class.java) // Update if needed
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

            // Back Button functionality (Fixed missing findViewById)
            findViewById<View>(R.id.backButton).setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

