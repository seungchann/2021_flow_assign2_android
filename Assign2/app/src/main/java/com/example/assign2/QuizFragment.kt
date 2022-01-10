package com.example.assign2

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.example.assign2.databinding.FragmentQuizBinding
import kotlinx.android.synthetic.main.answer_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.android.synthetic.main.fragment_quiz.actorImageView

class QuizFragment : Fragment() {
    private val TAG = "QuizFragment"
    lateinit var viewModel: MainViewModel
    private var _binding : FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val retrofitService = RetrofitService.getInstance()

    var testQuizDatas = mutableListOf<QuizData>()

    // Hint 관련 변수
    var isClickedDisk1: Boolean = false
    var isClickedDisk2: Boolean = false
    var isClickedDisk3: Boolean = false
    var isUsedHint1: Boolean = false
    var isUsedHint2: Boolean = false

    // DialogView 선언
    lateinit var dialogView: View
    lateinit var builder: AlertDialog.Builder
    lateinit var dialogImageView: ImageView
    lateinit var dialogVideoTitleTextView: TextView
    lateinit var dialogActorTextView: TextView
    lateinit var dialogSongTitleTextView: TextView
    lateinit var dialogArtistTitleTextView: TextView
    lateinit var dialogAnswerTextView: TextView
    lateinit var dialogNextButton: Button

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
        builder = AlertDialog.Builder(requireActivity())
        dialogView = layoutInflater.inflate(R.layout.answer_dialog_layout, null)

        binding.heartImageView.setImageResource(setHeartImageFromHeartNumber(viewModel.heartNumber))
        binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))

        makeTestDatas()

        return binding.root
    }

    fun Fragment.getViewModelStoreOwner(): ViewModelStoreOwner = try {
        requireActivity()
    } catch (e: IllegalStateException) {
        this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(viewModel.currentUser.profileURL).into(actorImageView)
        viewModel.getAllQuizDatas()
//        viewModel.QuizDataList.observe(viewLifecycleOwner, Observer {
//            Log.d(TAG, "onViewCreated: $it")
//            // Update UI
//            songTitleTextView.text = it.get(0).video_title
//
//        })

        setInitialDialogView()
        setButtons()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makeTestDatas() {
        testQuizDatas.add(QuizData(1, "첫눈처럼 너에게 가겠다", "에일리", "도깨비", 2016, "공유", "https://user-images.githubusercontent.com/49242646/148649473-126d6aa1-a625-4e68-a461-c76f519c3852.png", "도깨비명대사"))
        testQuizDatas.add(QuizData(2, "다시 난, 여기", "백예린", "사랑의 불시착", 2019, "현빈", "사랑의불시착이미지", "사랑의불시착명대사"))
    }

    fun setButtons() {
        binding.diskButton1.setOnClickListener {
            binding.turnTableButton.setBackgroundResource(R.drawable.turntable_1)
            isClickedDiskButton(1)
            Log.d("isClicked","1: ${isClickedDisk1.toString()}, 2: ${isClickedDisk2.toString()}, 3: ${isClickedDisk3.toString()}")
        }
        binding.diskButton2.setOnClickListener {
            binding.turnTableButton.setBackgroundResource(R.drawable.turntable_2)
            isClickedDiskButton(2)
            Log.d("isClicked","1: ${isClickedDisk1.toString()}, 2: ${isClickedDisk2.toString()}, 3: ${isClickedDisk3.toString()}")
        }
        binding.diskButton3.setOnClickListener {
            binding.turnTableButton.setBackgroundResource(R.drawable.turntable_3)
            isClickedDiskButton(3)
            Log.d("isClicked","1: ${isClickedDisk1.toString()}, 2: ${isClickedDisk2.toString()}, 3: ${isClickedDisk3.toString()}")
        }
        binding.turnTableButton.setOnClickListener {
            if(isClickedDisk2) {
                Log.d("isHintUsed","2_click: ${isClickedDisk2.toString()}, 2_hint: ${isUsedHint1.toString()}, 3_click: ${isClickedDisk3.toString()}, 3_hint: ${isUsedHint2.toString()}")
                viewModel.updateHintNumber(getHintNumberFromClickedButton(isClickedDisk2))
//                viewModel.hintNumber = viewModel.hintNumber - getHintNumberFromClickedButton(isClickedDisk2)
                binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))

            }
            if(isClickedDisk3) {
                Log.d("isHintUsed","2_click: ${isClickedDisk2.toString()}, 2_hint: ${isUsedHint1.toString()}, 3_click: ${isClickedDisk3.toString()}, 3_hint: ${isUsedHint2.toString()}")
                viewModel.updateHintNumber(getHintNumberFromClickedButton(isClickedDisk3))
//                viewModel.hintNumber = viewModel.hintNumber - getHintNumberFromClickedButton(isClickedDisk3)
                binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))
            }
        }

        binding.submitButton.setOnClickListener{
            checkAnswer()
            setAnswerDialogView()

            viewModel.QuizDataList.observe(viewLifecycleOwner, Observer {
            })
        }
    }


    // Heart, Hint 이미지 연결 부분

    fun setHeartImageFromHeartNumber(heartNum: Int): Int {
        return when (heartNum) {
            0 -> R.drawable.heart_0
            1 -> R.drawable.heart_1
            2 -> R.drawable.heart_2
            3 -> R.drawable.heart_3
            4 -> R.drawable.heart_4
            5 -> R.drawable.heart_5
            else -> R.drawable.heart_5
        }
    }

    fun setHintImageFromHintNumber(hintNum: Int): Int {
        return when (hintNum) {
            0 -> R.drawable.hint_0
            1 -> R.drawable.hint_1
            2 -> R.drawable.hint_2
            3 -> R.drawable.hint_3
            4 -> R.drawable.hint_4
            5 -> R.drawable.hint_5
            else -> R.drawable.hint_5
        }
    }

    fun isClickedDiskButton(num: Int) {
        isClickedDisk1 = false
        isClickedDisk2 = false
        isClickedDisk3 = false
        when (num) {
            1 -> isClickedDisk1 = true
            2 -> isClickedDisk2 = true
            3 -> isClickedDisk3 = true
        }
    }

    fun getHintNumberFromClickedButton(clicked: Boolean): Int {
        if (!isUsedHint1 && clicked == isClickedDisk2) {
            isUsedHint1 = true
            return 1
        }
        if (isUsedHint1 && !isUsedHint2 && clicked == isClickedDisk3) {
            isUsedHint2 = true
            return 1
        }
        if (!isUsedHint1 && !isUsedHint2 && clicked == isClickedDisk3) {
            isUsedHint1 = true
            isUsedHint2 = true
            return 2
        }
        else return 0
    }

    // DialogView 관련 코드

    fun setInitialDialogView() {
        dialogImageView = dialogView.findViewById<ImageView>(R.id.dialogImageView)
        dialogVideoTitleTextView = dialogView.findViewById<TextView>(R.id.videoTitleTextView)
        dialogActorTextView = dialogView.findViewById<TextView>(R.id.actorTextView)
        dialogSongTitleTextView = dialogView.findViewById<TextView>(R.id.dialogSongTitleTextView)
        dialogArtistTitleTextView = dialogView.findViewById<TextView>(R.id.dialogArtistTextView)
        dialogAnswerTextView = dialogView.findViewById<TextView>(R.id.answerTextView)
        dialogNextButton = dialogView.findViewById<Button>(R.id.nextButton)
        dialogNextButton.setOnClickListener {
            (activity as StartActivity).moveToNextQuizFragment(this)
        }
    }

    fun setAnswerDialogView() {

        // DB 연결 시에 변경해줘야 할 부분
        val answerQuizData = testQuizDatas[0]

        Glide.with(this).load(answerQuizData.image).into(dialogImageView)
        dialogVideoTitleTextView.text = answerQuizData.video_title
        dialogActorTextView.text = answerQuizData.actor1
        dialogSongTitleTextView.text = answerQuizData.songTitle
        dialogArtistTitleTextView.text = answerQuizData.artist

        builder.setView(dialogView)
            .setPositiveButton("다음", object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    (activity as StartActivity).moveToFragment(QuizFragment())
                    // 새로운 quizFragment 생성
                    Log.d("일단..", "positive")
                }
            } )
            .show()
    }


    // 수정할 부분
    fun checkAnswer(){
        var title = binding.songTitleTextView.text.toString()
        var artist = binding.artistTextView.text.toString()
        fun String.removeWhitespaces() = replace(" ", "")

        var isCorrect: Boolean = (dialogSongTitleTextView.text as String).removeWhitespaces() == title.removeWhitespaces()
                && (dialogArtistTitleTextView.text as String).removeWhitespaces() == artist.removeWhitespaces()

        // 오답인 경우
        if(!isCorrect){
            dialogAnswerTextView.text = "틀렸습니다."
            dialogAnswerTextView.setTextColor(Color.parseColor("#B00E23"))
            viewModel.updateHeartNumber()
            Log.d("QF_HEART_NUM",viewModel.heartNumber.toString())
            binding.heartImageView.setImageResource(setHeartImageFromHeartNumber(viewModel.heartNumber))
        }
        else { // 정답인 경우
            viewModel.updateCorrectNumber()
            Log.d("맞은 개수", viewModel.correctNumber.toString())
        }
    }
}
