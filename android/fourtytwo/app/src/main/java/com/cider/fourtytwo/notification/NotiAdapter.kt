package com.cider.fourtytwo.notification

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotiAdapter(private val context: Context, private val itemList : List<NotiItem>) :
    RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_noti, parent, false)
        return NotiViewHolder(view)
    }
    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.content.text = item.body
        holder.time.text = timeEdit(item.createdAt)
        Log.d(TAG, "onBindViewHolder 공감이모지: ${item.emoji}")
        when (item.emoji){
            "heart" -> {
                Glide.with(context).load(R.drawable.heart).into(holder.image)
            }
            "tear" -> {
                Glide.with(context).load(R.drawable.tear).into(holder.image)
            }
            "thumbsUp" -> {
                Glide.with(context).load(R.drawable.thumbsup).into(holder.image)
            }
            "fire" -> {
                Glide.with(context).load(R.drawable.fire).into(holder.image)
            }
        }
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class NotiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.noti_title)
        var content = itemView.findViewById<TextView>(R.id.noti_content)
        var time = itemView.findViewById<TextView>(R.id.noti_time)
        var image = itemView.findViewById<ImageView>(R.id.noti_image)
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