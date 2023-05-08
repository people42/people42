package com.cider.fourtytwo.feed

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf

class FeedAdapter(private val context: Context, private val itemList : List<RecentFeedData>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_feed, parent, false)
        return FeedViewHolder(view)
    }
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = itemList[position]
        Glide.with(context).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${itemList.get(position).recentMessageInfo.emoji}.gif").into(holder.emoji)
        holder.brushCnt.text = "${ item.recentMessageInfo.brushCnt.toString() }번 스친"
        holder.nickname.text = item.recentMessageInfo.nickname
        holder.content.text = item.recentMessageInfo.content
        holder.place.text = item.placeWithTimeInfo.placeName
        holder.time.text = timeEdit(item.placeWithTimeInfo.time)
        val myColor = item.recentMessageInfo.color
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
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(it, position)
        }
// 눌러놨던 공감버튼 표시
        if (item.recentMessageInfo.emotion != null) {
            when (item.recentMessageInfo.emotion) {
                "heart" -> holder.selectPlus.setImageResource(R.drawable.heart)
                "tear" -> holder.selectPlus.setImageResource(R.drawable.tear)
                "fire" -> holder.selectPlus.setImageResource(R.drawable.fire)
                "thumbsUp" ->  holder.selectPlus.setImageResource(R.drawable.thumbsup)
                else -> holder.selectPlus.setImageResource(R.drawable.baseline_add_24)
            }
        }
// 공감버튼 누르면 선택창 나옴
        val messageIdx = item.recentMessageInfo.messageIdx
        holder.messageReation.setOnClickListener {
            holder.messageReation.visibility = GONE
            holder.messageReactionSelect.visibility = VISIBLE
        }
        holder.selectPlus.setOnClickListener {
            holder.messageReation.visibility = GONE
            holder.messageReactionSelect.visibility = VISIBLE
        }
        holder.selectHeart.setOnClickListener {
            itemClickListener.onEmotionClick(it, position, "heart", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.heart)
            holder.messageReation.visibility = VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectFire.setOnClickListener {
            itemClickListener.onEmotionClick(it, position, "fire", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.fire)
            holder.messageReation.visibility = VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectTear.setOnClickListener {
            itemClickListener.onEmotionClick(it, position, "tear", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.tear)
            holder.messageReation.visibility = VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectThumbsup.setOnClickListener {
            itemClickListener.onEmotionClick(it, position, "thumbsUp", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.thumbsup)
            holder.messageReation.visibility = VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectCancel.setOnClickListener {
            itemClickListener.onEmotionClick(it, position, "delete", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.baseline_add_24)
            holder.messageReation.visibility = VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
//        holder.itemView.setOnTouchListener(object: OnSwipeTouchListener(context){
//            override fun onSwipeLeft() {
//                Toast.makeText(context,"왼쪽으로",Toast.LENGTH_SHORT).show()
//            }
//        })
//        holder.reaction.text = itemList.get(position).recentMessageInfo.content
    }
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
        fun onEmotionClick(v:View, position: Int, emotion : String, messageIdx : Int)
    }
    private lateinit var itemClickListener : OnItemClickListener
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
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
        var feedMessage = itemView.findViewById<RelativeLayout>(R.id.feed_message)
        var shadow1 = itemView.findViewById<ImageView>(R.id.feed_message_shadow1)
        var shadow2 = itemView.findViewById<ImageView>(R.id.feed_message_shadow2)
        var messageReation = itemView.findViewById<LinearLayout>(R.id.message_reation)
        var selectPlus = itemView.findViewById<ImageView>(R.id.select_plus)
        var messageReactionSelect = itemView.findViewById<LinearLayout>(R.id.message_reaction_select)
        var selectHeart = itemView.findViewById<ImageView>(R.id.select_heart)
        var selectFire = itemView.findViewById<ImageView>(R.id.select_fire)
        var selectTear = itemView.findViewById<ImageView>(R.id.select_tear)
        var selectThumbsup = itemView.findViewById<ImageView>(R.id.select_thumbsup)
        var selectCancel = itemView.findViewById<ImageView>(R.id.select_cancel)
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