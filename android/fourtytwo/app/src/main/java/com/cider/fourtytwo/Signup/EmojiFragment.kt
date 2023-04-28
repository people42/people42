package com.cider.fourtytwo.Signup

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cider.fourtytwo.R
import com.cider.fourtytwo.databinding.FragmentEmojiBinding


class EmojiFragment : Fragment(){
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
        val emojiAdapter = EmojiAdapter(requireContext())
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

        val emojiAdapter = EmojiAdapter(requireContext())
        emojiAdapter.setOnItemClickListener(object : EmojiAdapter.OnItemClickListener {
            override fun onItemClicked(data: Resources?) {
                Glide.with(requireActivity()).load(data).into(myEmojiView)
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}