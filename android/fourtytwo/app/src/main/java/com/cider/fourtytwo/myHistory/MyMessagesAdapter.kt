package com.cider.fourtytwo.myHistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cider.fourtytwo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyMessagesAdapter(private val context : Context, private val itemList : ArrayList<HistoryData>): RecyclerView.Adapter<MyMessagesAdapter.MessageViewHolder>(){
    interface OnHistoryClickListener {
        fun onHistoryClick(view: View, position: Int, messageIdx:Int)
    }
    private lateinit var historyClickListener: OnHistoryClickListener
    fun setOnHistoryClickListener(onHistoryClickListener: OnHistoryClickListener) {
        this.historyClickListener = onHistoryClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_last_message, parent, false)
        return MessageViewHolder(view)
    }
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.historyTime.text = timeEdit(itemList.get(position).createdAt)
        holder.historyText.text = itemList.get(position).content
        if (itemList[position].heart > 0){
            holder.history_reation_first.text = itemList[position].heart.toString()}
        if (itemList[position].tear > 0){
            holder.history_reation_second.text = itemList[position].tear.toString()}
        if (itemList[position].thumbsUp > 0){
            holder.history_reation_third.text = itemList[position].thumbsUp.toString()}
        if (itemList[position].fire > 0){
            holder.history_reation_fourth.text = itemList[position].fire.toString()}

        holder.itemView.setOnLongClickListener {
            val rotateAnimation = RotateAnimation(
                -1f, // 시작 각도
                1f, // 끝 각도
                Animation.RELATIVE_TO_SELF, // 회전 기준 X 좌표
                0.5f, // 회전 기준 X 좌표 (0 ~ 1)
                Animation.RELATIVE_TO_SELF, // 회전 기준 Y 좌표
                0.5f // 회전 기준 Y 좌표 (0 ~ 1)
            ).apply {
                duration = 100 // 애니메이션 시간 (ms)
                repeatMode = Animation.REVERSE // 애니메이션 반복 모드
                repeatCount = Animation.INFINITE // 애니메이션 반복 횟수
                interpolator = LinearInterpolator() // 애니메이션 인터폴레이터 설정
            }
            holder.itemView.startAnimation(rotateAnimation)
            holder.deleteButton.visibility = VISIBLE
            true
        }
        holder.itemView.setOnClickListener {
            holder.itemView.clearAnimation()
            holder.deleteButton.visibility = GONE
        }
        holder.deleteButton.setOnClickListener {
            historyClickListener?.onHistoryClick(it, position, itemList[position].messageIdx)
            holder.deleteButton.visibility = GONE
            holder.itemView.clearAnimation()
            itemList.removeAt(position);
            notifyItemRemoved(position);
        }
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var historyTime = itemView.findViewById<TextView>(R.id.history_time)
        var historyText = itemView.findViewById<TextView>(R.id.history_text)
        var history_reation_first = itemView.findViewById<TextView>(R.id.history_reaction_heart)
        var history_reation_second = itemView.findViewById<TextView>(R.id.history_reaction_tear)
        var history_reation_third = itemView.findViewById<TextView>(R.id.history_reaction_thumbs)
        var history_reation_fourth = itemView.findViewById<TextView>(R.id.history_reaction_fire)
        var view = itemView.findViewById<ImageView>(R.id.history_reation_first)
        var deleteButton = itemView.findViewById<ImageView>(R.id.history_delete)

    }

    fun timeEdit(inputDate: String): String? {
        val timeString = inputDate.substring(0, 16)
        val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val dateTime = LocalDateTime.parse(timeString, formatterInput)
        val formatterOutput = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        return formatterOutput.format(dateTime) // 2023년 05월 03일 00시 47분
    }

}