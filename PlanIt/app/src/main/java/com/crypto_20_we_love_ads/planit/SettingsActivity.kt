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

class SettingsActivity : AppCompatActivity() {
    private lateinit var switch: SwitchMaterial //okay...
    private lateinit var accentRandomizer: Button



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

        // changing the accent color
        // note: I could either import some random github library
        // OR I could generate a random six digit hex number to make the color.
        // one of these has a lot more whimsy

        //I resorted to chatgpt
        accentRandomizer = findViewById(R.id.accentPicker)

        accentRandomizer.setOnClickListener{
            // The root view of the activity

            // 1. Generate a random color
            val randomColor = generateRandomColor()

            // 2. Save the color to SharedPreferences
            saveColorToPreferences(this, randomColor)

            // 3. Apply the saved color to the root view's background
            applyColorToView(this, accentRandomizer)

        }
    }

    fun generateRandomColor(): Int {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)
        return Color.rgb(red, green, blue)
    }

    // Function to save the color to SharedPreferences
    private fun saveColorToPreferences(context: Context, color: Int) {
        val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("random_color", color)
            apply() // Save changes asynchronously
        }
    }

    // Function to retrieve the color from SharedPreferences
    private fun getColorFromPreferences(context: Context): Int {
        val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        return sharedPref.getInt("random_color", Color.BLACK) // Default to black if no value exists
    }

    // Function to apply the color to a UI element (in this case, the root view)
    private fun applyColorToView(context: Context, view: View) {
        val color = getColorFromPreferences(context)
        view.setBackgroundColor(color)  // Set the background color to the saved color
    }
}
