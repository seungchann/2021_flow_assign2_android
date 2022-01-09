package com.example.assign2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.example.assign2.databinding.FragmentQuizBinding
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment() {
    private val TAG = "QuizFragment"

    var testQuizDatas = mutableListOf<QuizData>()
    lateinit var viewModel: MainViewModel
    private var _binding : FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val retrofitService = RetrofitService.getInstance()

    lateinit var dialogView: View
    lateinit var builder: AlertDialog.Builder
    lateinit var dialogImage: ImageView
    lateinit var dialogVideoTitle: TextView
    lateinit var dialogActor: TextView
    lateinit var dialogArtist: TextView
    lateinit var dialogSongTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(getViewModelStoreOwner(), MyViewModelFactory(MainRepository(retrofitService))).get(MainViewModel::class.java)
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        testQuizDatas.add(QuizData(1, "첫눈처럼 너에게 가겠다", "에일리", "도깨비", 2016, "공유", "https://user-images.githubusercontent.com/49242646/148649473-126d6aa1-a625-4e68-a461-c76f519c3852.png", "도깨비명대사"))
        testQuizDatas.add(QuizData(2, "다시 난, 여기", "백예린", "사랑의 불시착", 2019, "현빈", "사랑의불시착이미지", "사랑의불시착명대사"))
        return binding.root
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
        
        builder = AlertDialog.Builder(requireActivity())
        dialogView = layoutInflater.inflate(R.layout.answer_dialog_layout, null)

        setInitialDialog()

        binding.submitBtn.setOnClickListener{
            Glide.with(this).load(testQuizDatas[0].image).into(dialogImage)
            dialogVideoTitle.text = answerTextView.text
            dialogActor.text = testQuizDatas[0].actor1
            dialogSongTitle.text = testQuizDatas[0].songTitle
            dialogArtist.text = testQuizDatas[0].artist

            builder.setView(dialogView)
                // .setPositiveButton("다음", nextButtonClick)
                .show()

            viewModel.QuizDataList.observe(viewLifecycleOwner, Observer {
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun Fragment.getViewModelStoreOwner(): ViewModelStoreOwner = try {
        requireActivity()
    } catch (e: IllegalStateException) {
        this
    }

    fun setInitialDialog() {
        dialogImage = dialogView.findViewById<ImageView>(R.id.dialogImage)
        dialogVideoTitle = dialogView.findViewById<TextView>(R.id.videoTitleText)
        dialogActor = dialogView.findViewById<TextView>(R.id.actorText)
        dialogSongTitle = dialogView.findViewById<TextView>(R.id.ostTitleText)
        dialogArtist = dialogView.findViewById<TextView>(R.id.artistText)
    }
}
