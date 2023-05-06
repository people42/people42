package com.cider.fourtytwo.person

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PersonAdapter(private val context: Context, val itemList : ArrayList<PersonPlaceInfo>) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_person_feed, parent, false)
        return PersonViewHolder(view)
    }
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.place.text = timeEdit(itemList[position].time)
        holder.content.text = itemList[position].content

        holder.itemView.setOnLongClickListener {
            // 리스트 형태의 다이얼로그
            val builder = AlertDialog.Builder(context)
            builder.setTitle("이 사용자를 신고하거나 차단하시겠습니까?")
                .setNegativeButton("신고") { dialog, id ->
                    personClickListener?.onPersonLongClick(it, position, id, itemList[position].messageIdx)
                }
                .setPositiveButton("차단") { dialog, id ->
                    personClickListener?.onPersonLongClick(it, position, id, 0)
                    itemList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNeutralButton("취소") { dialog, id ->
                }
            builder.show()
            true
        }
//        holder.reaction.text = itemList.get(position).recentMessageInfo.content
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView = itemView.findViewById(R.id.person_content)
        var place: TextView = itemView.findViewById(R.id.person_place)
//        var reaction = itemView.findViewById<ImageView>(R.id.message_reation)
    }
    interface OnPersonClickListener {
        fun onPersonLongClick(view: View, position: Int, id:Int, messageIdx:Int)
    }
    private lateinit var personClickListener: OnPersonClickListener
    fun setOnPersonClickListener(onPersonClickListener: OnPersonClickListener) {
        this.personClickListener = onPersonClickListener
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