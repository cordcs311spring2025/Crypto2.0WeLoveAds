package com.crypto_20_we_love_ads.planit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.random.Random
import android.content.Context
import android.graphics.Color
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class SettingsActivity : AppCompatActivity() {
    private lateinit var switch: SwitchMaterial




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings_screen)


        // Add Event button redirection
        findViewById<View>(R.id.addEvent).setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        // Home Button stays on the same page (no action needed)
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
    //switch for dark mode https://www.geeksforgeeks.org/how-to-implement-dark-night-mode-in-android-app/
        switch = findViewById(R.id.themeToggle) //breaks it

        switch.setOnClickListener {
            if(switch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // TODO change accent color setting (from set of colors, not a color picker)
        // do we want to be able to delete categories? It shouldn't be a problem since events can not have categories...
        // Category drop down FROM EditActivity.kt
        val catOptions =
            arrayOf("None", "Home", "Work", "Sport", "School", "Birthday", "Social", "Event")
        val catSel = findViewById<AutoCompleteTextView>(R.id.categoryPicker)
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, catOptions)
        catSel.setAdapter(catAdapter)
        catSel.setOnClickListener {
            catSel.showDropDown()
        }
        // TODO change category color setting
        // Defunct accent color changer
        // note: I could either import some random github library
        // OR I could generate a random six digit hex number to make the color.
        // one of these has a lot more whimsy





    }
}
