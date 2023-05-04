package com.cider.fourtytwo.Signup

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.cider.fourtytwo.R
import com.cider.fourtytwo.UserViewModel
import com.cider.fourtytwo.databinding.FragmentNicknameBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NicknameFragment : Fragment() {
    private var _binding : FragmentNicknameBinding? = null
    private val binding get() = _binding!!
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    private var pickedNickname: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ts_adj :TextSwitcher = binding.nicknameAdj
        ts_adj.setFactory{
            val txt = TextView(requireContext())
            txt.gravity = Gravity.CENTER
            txt.textSize = 25f
            txt.marginTop
            return@setFactory txt
        }
        val ts_noun :TextSwitcher = binding.nicknameNoun
        ts_noun.setFactory{
            val txt = TextView(requireContext())
            txt.gravity = Gravity.CENTER
            txt.textSize = 25f
            return@setFactory txt
        }
        getNickname()

        // 글자 이미지 터치 시 요청
        binding.pickNickname.setOnClickListener{
            getNickname()
        }
        // 다시 뽑기 버튼 터치 시 재요청
        binding.resetImage.setOnClickListener{
            getNickname()
        }
        // 완료 버튼 터치 시 다음 페이지로 이동
        binding.nicknameButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("myNickname", pickedNickname)
            Navigation.findNavController(view)
                .navigate(R.id.action_nicknameFragment_to_emojiFragment, bundle)
        }
    }
    fun getNickname() {
        var nicknameDivide : List<String>
        api.getNickname().enqueue(object : Callback<NicknameResponse> {
            override fun onResponse(call: Call<NicknameResponse>, response: Response<NicknameResponse>) {
                response.body()?.data?.let {
                    pickedNickname =  it.nickname
                    nicknameDivide = pickedNickname!!.split(" ")
                    binding.nicknameAdj.setText(nicknameDivide[0])
                    binding.nicknameNoun.setText(nicknameDivide[1])
                }
            }
            override fun onFailure(call: Call<NicknameResponse>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}