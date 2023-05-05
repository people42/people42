package com.cider.fourtytwo.feed

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        holder.time.text = timeEdit(itemList.get(position).placeWithTimeInfo.time)
        val myColor = itemList.get(position).recentMessageInfo.color
        ViewCompat.setBackgroundTintList(holder.feedMessage, ColorStateList.valueOf(Color.RED))
        // 내 말풍선 색
        holder.feedMessage.backgroundTintList = when (myColor) {
            "red" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            "orange" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            "yellow" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
            "green" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            "sky" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sky))
            "blue" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
            "purple" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple))
            else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_blue))
        }
        holder.shadow1.backgroundTintList = when (myColor) {
            "red" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            "orange" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            "yellow" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
            "green" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            "sky" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sky))
            "blue" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
            "purple" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple))
            else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_blue))
        }
        holder.shadow2.backgroundTintList = when (myColor) {
            "red" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            "orange" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            "yellow" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
            "green" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            "sky" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sky))
            "blue" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
            "purple" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple))
            else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_blue))
        }

//        holder.reaction.text = itemList.get(position).recentMessageInfo.content
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoji = itemView.findViewById<ImageView>(R.id.feedEmoji)
        var brushCnt = itemView.findViewById<TextView>(R.id.feedbrushCnt)
        var nickname = itemView.findViewById<TextView>(R.id.feedNickname)
        var content: TextView = itemView.findViewById(R.id.feedContent)
        var place: TextView = itemView.findViewById(R.id.feedLocation)
        var time: TextView = itemView.findViewById(R.id.feedTime)
//        var reaction = itemView.findViewById<ImageView>(R.id.message_reation)
        var feedMessage = itemView.findViewById<RelativeLayout>(R.id.feed_message)
        var shadow1 = itemView.findViewById<ImageView>(R.id.feed_message_shadow1)
        var shadow2 = itemView.findViewById<ImageView>(R.id.feed_message_shadow2)
    }
    fun timeEdit(inputDate : String): String? {
        val timeString = inputDate.substring(0, 16)
        val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val dateTime = LocalDateTime.parse(timeString, formatterInput)
        val formatterOutput = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        val outputDate = formatterOutput.format(dateTime)
        return outputDate // 2023년 05월 03일 00시 47분
    }
}