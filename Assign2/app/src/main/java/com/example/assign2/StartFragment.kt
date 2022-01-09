package com.example.assign2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assign2.databinding.FragmentStartBinding
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {

    lateinit var binding: FragmentStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStartBinding.inflate(layoutInflater)

        binding.gameStartButton.setOnClickListener {
            (activity as StartActivity).moveToFragment(QuizFragment())
        }

        binding.rankingCheckButton.setOnClickListener {
            (activity as StartActivity).moveToFragment(RankingFragment())
        }

        return binding.root
    }
}