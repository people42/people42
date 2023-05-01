package com.cider.fourtytwo.Signup

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import com.cider.fourtytwo.databinding.FragmentEmojiBinding
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import java.util.*


class EmojiFragment : Fragment(){
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
    private var _binding : FragmentEmojiBinding? = null
    private val binding get() = _binding!!
    var pickedEmoji = "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${emojiNameList[getRandomEmoji()]}.gif"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEmojiBinding.inflate(inflater, container, false)

//        val emojiBox = binding.emojiRecycler
//        val emojiAdapter = EmojiAdapter(requireContext())
//        emojiBox.adapter = emojiAdapter
//        emojiBox.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emojiButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("myNickname", arguments?.getString("myNickname"))
            bundle.putString("myEmoji", pickedEmoji)
            Navigation.findNavController(view)
                .navigate(R.id.action_emojiFragment_to_welcomeFragment, bundle)
        }
        // 내 이모지
        val myEmojiView: ImageView = binding.emojiPreview
        Glide.with(this).load(pickedEmoji).into(myEmojiView)
        // 변경
        myEmojiView.setOnClickListener {
            pickedEmoji = "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${emojiNameList[getRandomEmoji()]}.gif"
            Glide.with(this).load(pickedEmoji).into(myEmojiView)
        }
        binding.emojiresetImage.setOnClickListener {
            pickedEmoji = "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${emojiNameList[getRandomEmoji()]}.gif"
            Glide.with(this).load(pickedEmoji).into(myEmojiView)
        }
//        val emojiAdapter = EmojiAdapter(requireContext())
//        emojiAdapter.setOnItemClickListener(object : EmojiAdapter.OnItemClickListener {
//            override fun onItemClicked(v: View, data: String, pos: Int) {
//
//                Glide.with(requireActivity()).load(data).into(myEmojiView)
//                pickedEmoji = data
//            }
//        })

//        val emojiRecyclerView : RecyclerView = binding.emojiRecycler
//        emojiRecyclerView.adapter = AlphaInAnimationAdapter(EmojiAdapter(requireContext())).apply {
//            // Change the durations.
//            setDuration(1000)
//            // Change the interpolator.
//            setInterpolator(OvershootInterpolator())
//            // Disable the first scroll mode.
//            setFirstOnly(false)
//        }
            val alphaInAnimationAdapter = AlphaInAnimationAdapter(EmojiAdapter(requireContext()))
            alphaInAnimationAdapter.setDuration(100)
            alphaInAnimationAdapter.setInterpolator(OvershootInterpolator())
            alphaInAnimationAdapter.setFirstOnly(false)

    }
    fun getRandomEmoji(): Int {
        val random = Random()
        val num = random.nextInt(110)
        return num
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}