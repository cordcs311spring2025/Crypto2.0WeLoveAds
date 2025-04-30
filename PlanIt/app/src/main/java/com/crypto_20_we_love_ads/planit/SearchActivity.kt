package com.crypto_20_we_love_ads.planit

import android.content.Intent

import android.os.Bundle

import android.view.View

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.crypto_20_we_love_ads.planit.database.DatabaseHelper



private lateinit var dbHelper: DatabaseHelper
private lateinit var adapter: EventAdapter

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.search_screen)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EventSearchFragment())
                .commit()
        }



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


    }



}
