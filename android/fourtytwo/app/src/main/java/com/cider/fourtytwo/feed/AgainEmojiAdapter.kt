package com.cider.fourtytwo.feed

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R

class AgainEmojiAdapter(private val context: Context, private val emojiNameList : ArrayList<String>) : RecyclerView.Adapter<AgainEmojiAdapter.EmojiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_emoji, parent, false)
        return EmojiViewHolder(view)
    }
    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        Glide.with(context)
            .load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${emojiNameList[position]}.gif")
            .into(holder.emojiView)
    }
    override fun getItemCount(): Int {
        return emojiNameList.size
    }
    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiView: ImageView = itemView.findViewById(R.id.emoji_select_button)
    }
}
