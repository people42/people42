package com.cider.fourtytwo.Signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.cider.fourtytwo.R
import com.cider.fourtytwo.databinding.FragmentEmojiBinding

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emojiButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_emojiFragment_to_welcomeFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}