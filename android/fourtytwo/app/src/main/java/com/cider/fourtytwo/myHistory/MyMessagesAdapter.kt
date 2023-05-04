package com.cider.fourtytwo.myHistory

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cider.fourtytwo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyMessagesAdapter(private val itemList : List<HistoryData>): RecyclerView.Adapter<MyMessagesAdapter.MessageViewHolder>(), ItemTouchHelperListener{
    interface HistoryClickListener {
        fun onHistoryItemClick(view: View, position: Int)
        fun deleteMessage()
    }
    private var onitemClickListener: HistoryClickListener? = null
    fun setOnItemClickListener(listener: HistoryClickListener) {
        this.onitemClickListener = listener
    }

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_last_message, parent, false)
        return MessageViewHolder(view)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        Log.d(TAG, "어댑터에 데이터 도착: ${itemList}")
        holder.historyTime.text = timeEdit(itemList.get(position).createdAt)
        holder.historyText.text = itemList.get(position).content
        if (itemList[position].heart > 0){
            holder.history_reation_first.text = itemList[position].heart.toString()}
        if (itemList[position].tear > 0){
            holder.history_reation_second.text = itemList[position].tear.toString()}
        if (itemList[position].thumbsUp > 0){
            holder.history_reation_third.text = itemList[position].thumbsUp.toString()}
        if (itemList[position].fire > 0){
            holder.history_reation_fourth.text = itemList[position].fire.toString()}}
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

    }

    fun timeEdit(inputDate : String): String? {
        val timeString = inputDate.substring(0, 16)
        val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val dateTime = LocalDateTime.parse(timeString, formatterInput)
        val formatterOutput = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        val outputDate = formatterOutput.format(dateTime)
        return outputDate // 2023년 05월 03일 00시 47분
    }

    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onItemSwipe(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder?) {
        Log.d(TAG, "onRightClick: 오른쪽 눌림")

    }
}