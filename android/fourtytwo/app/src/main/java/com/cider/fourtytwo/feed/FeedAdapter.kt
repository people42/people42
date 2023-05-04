package com.cider.fourtytwo.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R

class FeedAdapter(private val context: Context, val itemList : List<RecentFeedData>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_feed, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        Glide.with(context).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${itemList.get(position).recentMessageInfo.emoji}.gif").into(holder.emoji)
        holder.brushCnt.text = "${ itemList.get(position).recentMessageInfo.brushCnt.toString() }번 스친"
        holder.nickname.text = itemList.get(position).recentMessageInfo.nickname
        holder.content.text = itemList.get(position).recentMessageInfo.content
        holder.place.text = itemList.get(position).placeWithTimeInfo.placeName
        holder.time.text = itemList.get(position).placeWithTimeInfo.time
//        holder.reaction.text = itemList.get(position).recentMessageInfo.content
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoji = itemView.findViewById<ImageView>(R.id.feedEmoji)
        var brushCnt = itemView.findViewById<TextView>(R.id.feedbrushCnt)
        var nickname = itemView.findViewById<TextView>(R.id.feedNickname)
        var content: TextView = itemView.findViewById<TextView>(R.id.feed_message)
        var place: TextView = itemView.findViewById<TextView>(R.id.feedLocation)
        var time: TextView = itemView.findViewById<TextView>(R.id.feedTime)
        var reaction = itemView.findViewById<ImageView>(R.id.message_reation)
    }
}