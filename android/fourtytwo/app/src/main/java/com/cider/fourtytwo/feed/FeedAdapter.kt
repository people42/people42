package com.cider.fourtytwo.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cider.fourtytwo.R

class FeedAdapter(val itemList: ArrayList<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_feed, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.content.text = itemList[position].content
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView = itemView.findViewById<TextView>(R.id.feed_message)
        var emoji = itemView.findViewById<ImageView>(R.id.message_emoji)
        var reaction = itemView.findViewById<ImageView>(R.id.message_reation)
//        val nickname:String =
//        val color:String =
//        val brushCnt: String =
//        val placeName:String =
//        val time: String =
//        val count: Boolean =
    }
}