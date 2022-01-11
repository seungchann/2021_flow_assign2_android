package com.example.assign2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.assign2.databinding.FragmentStartBinding
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {

    private val retrofitService = RetrofitService.getInstance()
    lateinit var binding: FragmentStartBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(getViewModelStoreOwner(), MyViewModelFactory(MainRepository(retrofitService))).get(MainViewModel::class.java)
//        binding.gameStartText.text = viewModel.currentUser.highestScore.toString()

        binding.gameStartButton.setOnClickListener {
            (activity as StartActivity).moveToFragment(QuizFragment())
        }

        binding.rankingCheckButton.setOnClickListener {
            (activity as StartActivity).moveToFragment(RankingFragment())
        }

        return binding.root
    }


    fun Fragment.getViewModelStoreOwner(): ViewModelStoreOwner = try {
        requireActivity()
    } catch (e: IllegalStateException) {
        this
    }
}