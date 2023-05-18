package com.cider.fourtytwo.signup

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R

class EmojiAdapter(private val context: Context) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    val emojiNameList : Array<String> = arrayOf(
        "alien",
        "angry-face",
        "anguished-face",
        "anxious-face-with-sweat",
        "beaming-face-with-smiling-eyes",
        "cat-with-tears-of-joy",
        "cat-with-wry-smile",
        "clown-face",
        "cold-face",
        "confounded-face",
        "confused-face",
        "cowboy-hat-face",
        "crying-cat",
        "crying-face",
        "disappointed-face",
        "disguised-face",
        "dizzy-face",
        "downcast-face-with-sweat",
        "drooling-face",
        "exploding-head",
        "face-blowing-a-kiss",
        "face-exhaling",
        "face-in-clouds",
        "face-savoring-food",
        "face-screaming-in-fear",
        "face-vomiting",
        "face-with-hand-over-mouth",
        "face-with-head-bandage",
        "face-with-medical-mask",
        "face-with-monocle",
        "face-with-open-mouth",
        "face-with-raised-eyebrow",
        "face-with-rolling-eyes",
        "face-with-spiral-eyes",
        "face-with-steam-from-nose",
        "face-with-symbols-on-mouth",
        "face-with-tears-of-joy",
        "face-with-thermometer",
        "face-with-tongue",
        "face-without-mouth",
        "fearful-face",
        "flushed-face",
        "frowning-face-with-open-mouth",
        "frowning-face",
        "ghost",
        "grimacing-face",
        "grinning-cat-with-smiling-eyes",
        "grinning-cat",
        "grinning-face-with-big-eyes",
        "grinning-face-with-smiling-eyes",
        "grinning-face-with-sweat",
        "grinning-face",
        "grinning-squinting-face",
        "hot-face",
        "hugging-face",
        "hushed-face",
        "kissing-cat",
        "kissing-face-with-closed-eyes",
        "kissing-face-with-smiling-eyes",
        "kissing-face",
        "loudly-crying-face",
        "lying-face",
        "money-mouth-face",
        "nauseated-face",
        "nerd-face",
        "neutral-face",
        "partying-face",
        "pensive-face",
        "persevering-face",
        "pile-of-poo",
        "pleading-face",
        "pouting-face",
        "purple-monster",
        "relieved-face",
        "robot",
        "rolling-on-the-floor-laughing",
        "sad-but-relieved-face",
        "shushing-face",
        "skull",
        "sleeping-face",
        "sleepy-face",
        "slightly-frowning-face",
        "slightly-smiling-face",
        "smiling-cat-with-heart-eyes",
        "smiling-face-with-halo",
        "smiling-face-with-heart-eyes",
        "smiling-face-with-hearts",
        "smiling-face-with-horns",
        "smiling-face-with-smiling-eyes",
        "smiling-face-with-sunglasses",
        "smiling-face-with-tear",
        "smiling-face",
        "smirking-face",
        "sneezing-face",
        "squinting-face-with-tongue",
        "star-struck",
        "thinking-face",
        "tired-face",
        "unamused-face",
        "upside-down-face",
        "weary-cat",
        "weary-face",
        "winking-face-with-tongue",
        "winking-face",
        "woozy-face",
        "worried-face",
        "yawning-face",
        "zany-face",
        "zipper-mouth-face")

    var pickedEmoji = String()

    interface OnItemClickListener {
        // url string
        fun onItemClicked(v:View, pickedEmoji: String, pos : Int)
    }

    private var onitemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onitemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_emoji, parent, false)
        val viewHolder: EmojiAdapter.EmojiViewHolder = EmojiViewHolder(view)

        return EmojiViewHolder(view)
    }
    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        pickedEmoji = "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${emojiNameList[position]}.gif"
        // Glide를 사용하여 이모지 GIF 설정
        Glide.with(context)
            .load(pickedEmoji)
            .into(holder.emojiView)
        holder.bind()

//        holder.emojiView.setOnClickListener {
//            onitemClickListener?.onItemClicked(pickedEmoji)
//        }
    }
    override fun getItemCount(): Int {
        return emojiNameList.size
    }
    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiView: ImageView = itemView.findViewById(R.id.emoji_select_button)
        val pos = adapterPosition
        fun bind() {
            if(pos!= RecyclerView.NO_POSITION) {
                emojiView.setOnClickListener {
                    onitemClickListener?.onItemClicked(itemView, pickedEmoji, pos)
                }
            }
        }
    }

}
