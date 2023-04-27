package com.cider.fourtytwo.Signup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R

class EmojiAdapter(private val context: Context,
                   private val emojiList: List<String>) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_emoji, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val resourceId = emojiList[position]

        // Glide를 사용하여 이모지 GIF 설정
        Glide.with(context)
            .load(resourceId)
            .into(holder.emojiView)
    }

    override fun getItemCount(): Int {
        return emojiList.size
    }

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiView: ImageView = itemView.findViewById(R.id.emoji_select_button)
    }

//    fun getemoji(emojiName:String) {
//
//    }
}