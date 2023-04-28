package com.cider.fourtytwo.Signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.cider.fourtytwo.R
import com.cider.fourtytwo.databinding.FragmentNicknameBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.NicknameResponse
import com.cider.fourtytwo.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NicknameFragment : Fragment() {
    private var _binding : FragmentNicknameBinding? = null
    private val binding get() = _binding!!
    val api = RetrofitInstance.getInstance().create(Api::class.java)
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
            Navigation.findNavController(view)
                .navigate(R.id.action_nicknameFragment_to_emojiFragment)
        }
    }
    fun getNickname() {
        var nicknameDivide : List<String>
        api.getNickname().enqueue(object : Callback<NicknameResponse> {
            override fun onResponse(call: Call<NicknameResponse>, response: Response<NicknameResponse>) {
                Log.d("log",response.toString())
                response.body()?.data?.let {
                    val newNickname: String =  it.nickname
                    nicknameDivide = newNickname.split(" ")
                    binding.nicknameAdj.text = nicknameDivide[0]
                    binding.nicknameNoun.text = nicknameDivide[1]
                    if (nicknameDivide[0].length > 6 || nicknameDivide[1].length > 6)
                    {
                        binding.nicknameAdj.setTextSize(Dimension.SP, 20.0f)
                        binding.nicknameNoun.setTextSize(Dimension.SP, 20.0f)
                    }
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