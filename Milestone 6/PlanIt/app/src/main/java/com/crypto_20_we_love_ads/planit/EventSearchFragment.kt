package com.crypto_20_we_love_ads.planit
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crypto_20_we_love_ads.planit.database.DatabaseHelper


// Data model
data class EventItem(
    val id: Int,
    val title: String,
    val time: String,
    val date: String,
    val location: String,
    val importance: Int
)

class EventSearchFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_event_search, container, false)


        dbHelper = DatabaseHelper(requireContext())
        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.eventRecyclerView)
        adapter = EventAdapter(emptyList()) { item ->
            val intent = Intent(requireContext(), EditActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("title", item.title)
            intent.putExtra("time", item.time)
            intent.putExtra("date", item.date)
            intent.putExtra("location", item.location)
            intent.putExtra("importance", item.importance)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                displayEvents(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                displayEvents(newText)
                return true
            }
        })

        return view
    }



    private fun displayEvents(query: String) {
        val results = dbHelper.searchItems(query)
        adapter.updateList(results)
    }
}
