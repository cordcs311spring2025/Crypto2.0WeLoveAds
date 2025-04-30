package com.crypto_20_we_love_ads.planit


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class EventAdapter(
    private var items: List<EventItem>,
    private val onItemClick: (EventItem) -> Unit
) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.currentEventName)
        val time: TextView = itemView.findViewById(R.id.currentEventTime)
        val date: TextView = itemView.findViewById(R.id.currentEventDate)
        val location: TextView = itemView.findViewById(R.id.currentLocationText)
        val important: TextView = itemView.findViewById(R.id.important)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.title
        holder.time.text = item.time
        holder.date.text = item.date
        holder.location.text = item.location
        holder.important.text = when (item.importance) {
            1 -> "!"
            2 -> "!!"
            3 -> "!!!"
            else -> ""
        }
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
    override fun getItemCount() = items.size

    fun updateList(newItems: List<EventItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
