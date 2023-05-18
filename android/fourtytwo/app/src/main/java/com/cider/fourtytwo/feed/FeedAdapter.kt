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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf

class FeedAdapter(private val context: Context, private val itemList : List<RecentFeedData>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_newfeed, parent, false)
        return FeedViewHolder(view)
    }
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = itemList[position]
        holder.place.text = "${item.placeWithTimeInfo.placeName} 근처"
        holder.time.text = isTodayOrYesterday(item.placeWithTimeInfo.time)
        holder.nickname.text = "${item.recentUsersInfo.nickname}님 등"
        holder.brushCnt.text = "${ item.recentUsersInfo.userCnt }명과 스쳤습니다."

        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(it, position)
        }
        val first = item.recentUsersInfo.firstTimeUserEmojis
        val again = item.recentUsersInfo.repeatUserEmojis
        if (first.isNotEmpty()){
            holder.firstTimeUserEmojis.adapter = AgainEmojiAdapter(context, first)
            holder.firstTimeUserEmojis.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.first.text = "처음 만난 ${first.size}명"
            holder.first.visibility = VISIBLE
            holder.firstTimeUserEmojis.visibility = VISIBLE
        } else {
            holder.first.visibility = GONE
            holder.firstTimeUserEmojis.visibility = GONE
        }
        if (again.isNotEmpty()) {
            holder.repeatUserEmojis.adapter = AgainEmojiAdapter(context, again)
            holder.repeatUserEmojis.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.again.text = "다시 만난 ${again.size}명"
            holder.again.visibility = VISIBLE
            holder.repeatUserEmojis.visibility = VISIBLE
        } else {
            holder.again.visibility = GONE
            holder.repeatUserEmojis.visibility = GONE
        }
    }
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    private lateinit var itemClickListener : OnItemClickListener
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var place = itemView.findViewById<TextView>(R.id.new_place)
        var time = itemView.findViewById<TextView>(R.id.new_time)
        var nickname = itemView.findViewById<TextView>(R.id.new_nickname)
        var brushCnt = itemView.findViewById<TextView>(R.id.new_brushCnt)
        var again = itemView.findViewById<TextView>(R.id.new_again)
        var first = itemView.findViewById<TextView>(R.id.new_first)
        val firstTimeUserEmojis = itemView.findViewById<RecyclerView>(R.id.new_recyclerFirst)
        val repeatUserEmojis: RecyclerView = itemView.findViewById(R.id.new_recyclerAgain)
    }
    fun isTodayOrYesterday(text: String): String {
        val timeString = text.substring(0, 16)  // "yyyy-MM-dd'T'HH:mm"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm") // 문자열 -> 시간
        val dateTime = LocalDateTime.parse(timeString, formatter).toLocalDate() // 바꿔라
        val today = LocalDate.now(ZoneId.of("Asia/Seoul")) // 오늘 시간
        val yesterday = today.minusDays(1) // 어제
        var returnString = "${timeString.substring(8, 10)}일 ${timeString.substring(12, 14)}시" // "
        if (dateTime == today) {
            returnString = "오늘 ${text.substring(11, 13)}시 쯤"
        } else if (dateTime == yesterday) {
            returnString = "어제 ${text.substring(11, 13)}시 쯤"
        }
        return returnString
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