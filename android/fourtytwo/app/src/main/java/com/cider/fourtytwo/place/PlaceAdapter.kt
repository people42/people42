package com.cider.fourtytwo.place

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
import android.widget.LinearLayout
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

class PlaceAdapter(private val context: Context, val itemList : ArrayList<MessagesInfo>) :
    RecyclerView.Adapter<PlaceAdapter.FeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_feed, parent, false)
        return FeedViewHolder(view)
    }
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        Glide.with(context).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${itemList.get(position).emoji}.gif").into(holder.emoji)
        val brush = itemList[position].brushCnt
        val item = itemList[position]
        val myColor = itemList[position].color
        holder.brushCnt.text = "${ brush }번 스친"
        holder.nickname.text = itemList[position].nickname
        holder.content.text = itemList[position].content
        ViewCompat.setBackgroundTintList(holder.feedMessage, ColorStateList.valueOf(Color.RED))

        val shadowRadius = 5f
        val shadowDx = 0f
        val shadowDy = 0f

        val colorList = HashMap<String, Int>()
        colorList["red"] = R.color.red
        colorList["orange"] = R.color.orange
        colorList["yellow"] = R.color.yellow
        colorList["green"] = R.color.green
        colorList["sky"] = R.color.sky
        colorList["blue"] = R.color.blue
        colorList["purple"] = R.color.purple
        colorList["pink"] = R.color.pink
        colorList[myColor]?.let {
            // 컬러 리소스에서 실제 컬러로 변환
            val shadowColor = holder.brushCnt.context.resources.getColor(it)
            holder.brushCnt.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
            holder.nickname.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
        }
        // 내 말풍선 색
        holder.feedMessage.backgroundTintList = when (myColor) {
            "red" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            "orange" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            "yellow" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
            "green" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            "sky" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sky))
            "blue" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
            "purple" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple))
            "pink" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink))
            else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_blue))
        }
        if (brush > 1){
            holder.shadow1.backgroundTintList = when (myColor) {
                "red" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
                "orange" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
                "yellow" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
                "green" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                "sky" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sky))
                "blue" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
                "purple" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple))
                "pink" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink))
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
                "pink" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink))
                else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_blue))
            }
        } else {
            holder.shadow1.visibility = GONE
            holder.shadow2.visibility = GONE
        }
        holder.itemView.setOnLongClickListener {
            // 리스트 형태의 다이얼로그
            val builder = AlertDialog.Builder(context)
            builder.setTitle("이 사용자를 신고하거나 차단하시겠습니까?")
                .setNegativeButton("신고") { dialog, id ->
                    placeClickListener.onPlaceLongClick(it, position, id, itemList[position].messageIdx, 0)
                }
                .setPositiveButton("차단") { dialog, id ->
                    placeClickListener.onPlaceLongClick(it, position, id, 0, itemList[position].userIdx)
                    itemList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNeutralButton("취소") { dialog, id ->
                }
            builder.show()
            true
        }
        holder.itemView.setOnClickListener {
            placeClickListener.onPlaceClick(it, position, itemList[position].userIdx, itemList[position].nickname)
        }
        // 눌러놨던 공감버튼 표시
        if (item.emotion != null) {
            when (item.emotion) {
                "heart" -> holder.selectPlus.setImageResource(R.drawable.heart)
                "tear" -> holder.selectPlus.setImageResource(R.drawable.tear)
                "fire" -> holder.selectPlus.setImageResource(R.drawable.fire)
                "thumbsUp" ->  holder.selectPlus.setImageResource(R.drawable.thumbsup)
                else -> holder.selectPlus.setImageResource(R.drawable.baseline_add_24)
            }
        }
// 공감버튼 누르면 선택창 나옴
        val messageIdx = item.messageIdx
        holder.messageReation.setOnClickListener {
            holder.messageReation.visibility = GONE
            holder.messageReactionSelect.visibility = View.VISIBLE
        }
        holder.selectPlus.setOnClickListener {
            holder.messageReation.visibility = GONE
            holder.messageReactionSelect.visibility = View.VISIBLE
        }
        holder.selectHeart.setOnClickListener {
            placeClickListener.onEmotionClick(it, position, "heart", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.heart)
            holder.messageReation.visibility = View.VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectFire.setOnClickListener {
            placeClickListener.onEmotionClick(it, position, "fire", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.fire)
            holder.messageReation.visibility = View.VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectTear.setOnClickListener {
            placeClickListener.onEmotionClick(it, position, "tear", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.tear)
            holder.messageReation.visibility = View.VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectThumbsup.setOnClickListener {
            placeClickListener.onEmotionClick(it, position, "thumbsUp", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.thumbsup)
            holder.messageReation.visibility = View.VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
        holder.selectCancel.setOnClickListener {
            placeClickListener.onEmotionClick(it, position, "delete", messageIdx)
            holder.selectPlus.setImageResource(R.drawable.baseline_add_24)
            holder.messageReation.visibility = View.VISIBLE
            holder.messageReactionSelect.visibility = GONE
        }
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoji = itemView.findViewById<ImageView>(R.id.feedEmoji)
        var brushCnt = itemView.findViewById<TextView>(R.id.feedbrushCnt)
        var nickname = itemView.findViewById<TextView>(R.id.feedNickname)
        var content: TextView = itemView.findViewById(R.id.feedContent)
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
    interface OnPlaceClickListener {
        fun onPlaceClick(view: View, position: Int,userIdx: Int, nickname:String)
        fun onPlaceLongClick(view: View, position: Int, id:Int, messageIdx:Int, userIdx:Int)
        fun onEmotionClick(v:View, position: Int, emotion : String, messageIdx : Int)

    }
    private lateinit var placeClickListener: OnPlaceClickListener
    fun setOnPlaceClickListener(onHistoryClickListener: OnPlaceClickListener) {
        this.placeClickListener = onHistoryClickListener
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