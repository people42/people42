package com.cider.fourtytwo.Signup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cider.fourtytwo.App
import com.cider.fourtytwo.R
import com.cider.fourtytwo.databinding.FragmentEmojiBinding
import com.cider.fourtytwo.feed.FeedAdapter
import okhttp3.OkHttpClient

class EmojiFragment : Fragment() {
    private var _binding : FragmentEmojiBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEmojiBinding.inflate(inflater, container, false)

        val emojiBox = binding.emojiRecycler
        val emojiItem = ArrayList<String>()
        emojiItem.add("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/alien.gif")
        emojiItem.add("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/cat-with-tears-of-joy.gif")
        emojiItem.add("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/face-in-clouds.gif")
        val emojiAdapter = EmojiAdapter(requireContext(), emojiItem)
        emojiBox.adapter = emojiAdapter
        emojiBox.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emojiButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_emojiFragment_to_welcomeFragment)
        }
        // 내 이모지
        val myEmojiView: ImageView = binding.emojiPreview
        Glide.with(this).load(R.raw.robot).into(myEmojiView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}