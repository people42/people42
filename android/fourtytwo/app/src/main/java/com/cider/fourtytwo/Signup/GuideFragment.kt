package com.cider.fourtytwo.Signup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.databinding.FragmentGuideBinding
import com.cider.fourtytwo.databinding.FragmentWelcomeBinding

class GuideFragment : Fragment() {
    private var _binding : FragmentGuideBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGuideBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.writeLater.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_guideFragment_to_MainActivity)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
