package com.example.assign2

import android.content.Context
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.assign2.databinding.FragmentQuizBinding
import com.example.assign2.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    private var _binding : FragmentRankingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCurrentUserRanking()
    }
    fun setCurrentUserRanking() {
        if (viewModel.currentUser.profileURL == "null") {
            viewModel.currentUser.profileURL = "https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg"
        }
        Glide.with(this).load(viewModel.currentUser.profileURL).into(binding.profileImageView)
        binding.highestScoreTextView.text = viewModel.currentUser.highestScore.toString()
        binding.userNameBlueTextView.text = viewModel.currentUser.nickName
        binding.userNameTextView.text = viewModel.currentUser.nickName
        binding.userEmailTextView.text = viewModel.currentUser.email
    }
}