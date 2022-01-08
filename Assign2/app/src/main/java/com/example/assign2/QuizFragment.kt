package com.example.assign2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.example.assign2.databinding.FragmentQuizBinding
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment() {
    private val TAG = "QuizFragment"

    lateinit var viewModel: MainViewModel
    private val retrofitService = RetrofitService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(getViewModelStoreOwner(), MyViewModelFactory(MainRepository(retrofitService))).get(MainViewModel::class.java)
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.QuizDataList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onViewCreated: $it")
            // Update UI
            answerTextView.text = it.get(0).video_title
            Glide.with(this).load(viewModel.currentUser.profileURL).into(profileImageView)
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {

        })
        viewModel.getAllQuizDatas()
    }

    fun Fragment.getViewModelStoreOwner(): ViewModelStoreOwner = try {
        requireActivity()
    } catch (e: IllegalStateException) {
        this
    }
}
