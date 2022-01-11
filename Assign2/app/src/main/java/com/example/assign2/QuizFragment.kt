package com.example.assign2

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.example.assign2.databinding.FragmentQuizBinding
import kotlinx.android.synthetic.main.answer_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.android.synthetic.main.fragment_quiz.actorImageView
import kotlin.concurrent.timer
import android.content.Context.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService




class QuizFragment : Fragment() {
    private val TAG = "QuizFragment"
    lateinit var viewModel: MainViewModel
    private var _binding : FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val retrofitService = RetrofitService.getInstance()

    var testQuizDatas = mutableListOf<QuizData>()
    lateinit var currentQuizData: QuizData

    // Hint 관련 변수
    var isClickedDisk1: Boolean = true
    var isClickedDisk2: Boolean = false
    var isClickedDisk3: Boolean = false
    var isUsedHint1: Boolean = false
    var isUsedHint2: Boolean = false

    // DialogView 선언
    lateinit var alertDialog: AlertDialog
    lateinit var dialogView: View
    lateinit var builder: AlertDialog.Builder
    lateinit var dialogImageView: ImageView
    lateinit var dialogVideoTitleTextView: TextView
    lateinit var dialogActorTextView: TextView
    lateinit var dialogSongTitleTextView: TextView
    lateinit var dialogArtistTitleTextView: TextView
    lateinit var dialogAnswerTextView: TextView
    lateinit var dialogNextButton: Button

    // SoundPool
    private var playFlag = false

    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private var spLoaded = false

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

        if (viewModel.QuizDataList.count() == 0) {
            viewModel.getAllQuizDatas()
        }

        return binding.root
    }

    fun getCurrentQuizFromViewModel() {
        val randomNum = (0..viewModel.QuizDataList.count()).random()
        this.currentQuizData = viewModel.QuizDataList[randomNum]
        viewModel.QuizDataList.removeAt(randomNum)
    }

    fun Fragment.getViewModelStoreOwner(): ViewModelStoreOwner = try {
        requireActivity()
    } catch (e: IllegalStateException) {
        this
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Glide.with(this).load(viewModel.currentUser.profileURL).into(actorImageView)

        getCurrentQuizFromViewModel()
        // binding.songTitleTextView.imeOptions(EditorInfo.IME_ACTION_NEXT)

        // 답 입력할 때 키보드 엔터 이벤트
        binding.songTitleTextView
            .setOnEditorActionListener{ textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {

                handled = true
            }
            handled
        }
        binding.artistTextView
            .setOnEditorActionListener{ textView, action, event ->
                var handled = false
                if (action == EditorInfo.IME_ACTION_DONE) {
                    //fragment 키보드 내리기
                    val mInputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    mInputMethodManager.hideSoftInputFromWindow(
                        binding.artistTextView.getWindowToken(),
                        0
                    )
                    checkAnswer()
                    setAnswerDialogView()
                    handled = true
                }
                handled
            }

        Glide.with(this).load(viewModel.currentUser.profileURL).into(actorImageView)
        viewModel.getAllQuizDatas()
//        viewModel.QuizDataList.observe(viewLifecycleOwner, Observer {
//            Log.d(TAG, "onViewCreated: $it")
//            // Update UI
//            songTitleTextView.text = it.get(0).video_title
//
//        })

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .build()
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (status == 0) {
                spLoaded = true
            } else {
                Toast.makeText(activity as StartActivity, "Gagal load", Toast.LENGTH_SHORT).show()
            }
        }
        soundId = soundPool.load(activity as StartActivity, R.raw.snow, 1)

        setInitialDialogView()
        setTurnTableButton()
        setButtons()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setTurnTableButton() {
        binding.turnTableButton.setOnClickListener {
            // lp안에 재생 버튼 누르면 재생

            if(isClickedDisk1) {
                playMusicWithDuration(1)
            }

            if(isClickedDisk2) {
                Log.d("isHintUsed","2_click: ${isClickedDisk2.toString()}, 2_hint: ${isUsedHint1.toString()}, 3_click: ${isClickedDisk3.toString()}, 3_hint: ${isUsedHint2.toString()}")
                viewModel.updateHintNumber(getHintNumberFromClickedButton(isClickedDisk2))
                binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))
                playMusicWithDuration(3)

            }
            if(isClickedDisk3) {
                Log.d("isHintUsed","2_click: ${isClickedDisk2.toString()}, 2_hint: ${isUsedHint1.toString()}, 3_click: ${isClickedDisk3.toString()}, 3_hint: ${isUsedHint2.toString()}")
                viewModel.updateHintNumber(getHintNumberFromClickedButton(isClickedDisk3))
                binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))
                playMusicWithDuration(5)
            }
        }
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

        binding.submitButton.setOnClickListener{
            checkAnswer()
            setAnswerDialogView()
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
        alertDialog = builder.create()
        dialogImageView = dialogView.findViewById<ImageView>(R.id.dialogImageView)
        dialogVideoTitleTextView = dialogView.findViewById<TextView>(R.id.videoTitleTextView)
        dialogActorTextView = dialogView.findViewById<TextView>(R.id.actorTextView)
        dialogSongTitleTextView = dialogView.findViewById<TextView>(R.id.dialogSongTitleTextView)
        dialogArtistTitleTextView = dialogView.findViewById<TextView>(R.id.dialogArtistTextView)
        dialogAnswerTextView = dialogView.findViewById<TextView>(R.id.answerTextView)
        dialogNextButton = dialogView.findViewById<Button>(R.id.nextButton)
        dialogNextButton.setOnClickListener {
            if (viewModel.isGameOver()) {
                viewModel.heartNumber = 5
                viewModel.hintNumber = 5
                (activity as StartActivity).moveToFragment(StartFragment())
            } else {
                (activity as StartActivity).moveToNextQuizFragment()
            }
            alertDialog.dismiss()
        }
    }

    fun setAnswerDialogView() {

        // DB 연결 시에 변경해줘야 할 부분
        val answerQuizData = currentQuizData

        Glide.with(this).load(answerQuizData.image).into(dialogImageView)
        dialogVideoTitleTextView.text = answerQuizData.video_title
        dialogActorTextView.text = answerQuizData.actor1
        dialogSongTitleTextView.text = answerQuizData.songTitle
        dialogArtistTitleTextView.text = answerQuizData.artist

        alertDialog.setView(dialogView)
        alertDialog.show()
    }


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

    fun playMusicWithDuration(sec: Int) {
        if (spLoaded) {
            if(playFlag == false){ // 재생되고 있지 않은 경우
//                    binding.playBtn.setBackgroundResource(R.drawable.pause)
                playFlag = true
                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                var second : Int = 0
                timer(period = 1000, initialDelay = 1000){
                    second++
                    if(second==sec){
                        pauseAudio()
                        cancel()
                    }
                }
            }
            else { // 재생되고 있는 경우
                pauseAudio()
            }
        }
    }

    fun pauseAudio(){
//        binding.turnTableButton.setBackgroundResource(R.drawable.play_circle)
        playFlag = false
        soundPool.autoPause()
    }

}

