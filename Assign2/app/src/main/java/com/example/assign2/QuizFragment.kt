package com.example.assign2

import android.graphics.Color
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.example.assign2.databinding.FragmentQuizBinding
import kotlinx.android.synthetic.main.answer_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlin.concurrent.timer
import android.content.Context.INPUT_METHOD_SERVICE
import de.hdodenhof.circleimageview.CircleImageView

import kotlinx.android.synthetic.main.fragment_ranking.*
import org.w3c.dom.Text


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
    lateinit var dialogDateTextView: TextView
    lateinit var dialogActorImageView: CircleImageView
    lateinit var dialogActorImageView2: CircleImageView
    lateinit var dialogActorImageView3: CircleImageView
    lateinit var dialogActorTextView: TextView
    lateinit var dialogActorTextView2: TextView
    lateinit var dialogActorTextView3: TextView
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
        soundId = soundPool.load(activity as StartActivity, getMusicFromTitle(currentQuizData.songTitle), 1)

        setInitialDialogView()
        setTurnTableButton()
        setButtons()
        setCurrentUserProfile()
    }

    fun getMusicFromTitle(title: String): Int {
        return when(title) {
            "첫눈처럼 너에게 가겠다" -> R.raw.iwillgotoyoulikethefirstsnow
            "내일" -> R.raw.tomorrow
            "만약에" -> R.raw.ift
            "너의 모든 순간" -> R.raw.everymomentofyou
            "두근두근" -> R.raw.pitapating
            "나타나" -> R.raw.appear
            "너와 나의 시간은" -> R.raw.ourtime
            "시간을 거슬러" -> R.raw.backintime
            "너였다면" -> R.raw.ifitisyou
            "마음을 드려요" -> R.raw.giveyoumyheart
            "흔들리는 꽃들 속에서 네 샴푸향이 느껴진거야" -> R.raw.yourshampooscentintheflowers
            "그대를 잊는다는 건" -> R.raw.forgettingyou
            "ALWAYS" -> R.raw.always
            "괜찮아 사랑이야" -> R.raw.itsalrightitslove
            "눈꽃" -> R.raw.snowonthebranches
            "시작" -> R.raw.start
            "모든 날, 모든 순간" -> R.raw.everydayeverymoment
            "소녀" -> R.raw.alittlegirl
            "미운사람" -> R.raw.hatefulperson
            "묘해, 너와" -> R.raw.itsstrangewithyou
            "어른" -> R.raw.adult
            "내 손을 잡아" -> R.raw.holdmyhand
            "기억해줘요 내 모든 날과 그때를" -> R.raw.rememeberme
            "너에게" -> R.raw.toyou
            "그대란 정원" -> R.raw.youaremygarden
            else -> R.raw.iwillgotoyoulikethefirstsnow
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setTurnTableButton() {
        binding.turnTableButton.setOnClickListener {
            // lp안에 재생 버튼 누르면 재생

            if(isClickedDisk1) {
                playMusicWithDuration(2)
            }

            if(isClickedDisk2) {
                Log.d("isHintUsed","2_click: ${isClickedDisk2.toString()}, 2_hint: ${isUsedHint1.toString()}, 3_click: ${isClickedDisk3.toString()}, 3_hint: ${isUsedHint2.toString()}")
                viewModel.updateHintNumber(getHintNumberFromClickedButton(isClickedDisk2))
                binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))
                playMusicWithDuration(4)

            }
            if(isClickedDisk3) {
                Log.d("isHintUsed","2_click: ${isClickedDisk2.toString()}, 2_hint: ${isUsedHint1.toString()}, 3_click: ${isClickedDisk3.toString()}, 3_hint: ${isUsedHint2.toString()}")
                viewModel.updateHintNumber(getHintNumberFromClickedButton(isClickedDisk3))
                binding.hintImageView.setImageResource(setHintImageFromHintNumber(viewModel.hintNumber))
                playMusicWithDuration(6)
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

    fun setCurrentUserProfile() {
        if (viewModel.currentUser.profileURL == "null") {
            viewModel.currentUser.profileURL = "https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg"
        }
        Glide.with(this).load(viewModel.currentUser.profileURL).into(binding.quizProfileImageView)
        binding.profileNameTextView.text = viewModel.currentUser.nickName
        binding.scoreTextView.text = viewModel.correctNumber.toString()
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
        dialogDateTextView = dialogView.findViewById<TextView>(R.id.videoDateTextView)
        dialogActorTextView = dialogView.findViewById<TextView>(R.id.actorTextView)
        dialogActorTextView2 = dialogView.findViewById<TextView>(R.id.actorTextView2)
        dialogActorTextView3 = dialogView.findViewById<TextView>(R.id.actorTextView3)
        dialogActorImageView = dialogView.findViewById<CircleImageView>(R.id.quizProfileImageView)
        dialogActorImageView2 = dialogView.findViewById<CircleImageView>(R.id.actorImageView2)
        dialogActorImageView3 = dialogView.findViewById<CircleImageView>(R.id.actorImageView3)
        dialogSongTitleTextView = dialogView.findViewById<TextView>(R.id.dialogSongTitleTextView)
        dialogArtistTitleTextView = dialogView.findViewById<TextView>(R.id.dialogArtistTextView)
        dialogAnswerTextView = dialogView.findViewById<TextView>(R.id.answerTextView)
        dialogNextButton = dialogView.findViewById<Button>(R.id.nextButton)
        dialogNextButton.setOnClickListener {
            if (viewModel.isGameOver()) {
                viewModel.heartNumber = 5
                viewModel.hintNumber = 5

                // high-score 비교해서 업데이트
                if (viewModel.correctNumber > viewModel.currentUser.highestScore) {
                    // query로 업데이트
                }
                viewModel.correctNumber = 0
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
        Glide.with(this).load(answerQuizData.profile_image1).into(dialogActorImageView)
        Glide.with(this).load(answerQuizData.profile_image2).into(dialogActorImageView2)
        Glide.with(this).load(answerQuizData.profile_image3).into(dialogActorImageView3)

        dialogVideoTitleTextView.text = answerQuizData.video_title
        dialogDateTextView.text = "(${answerQuizData.date}년)"
        dialogActorTextView.text = answerQuizData.actor1
        dialogActorTextView2.text = answerQuizData.actor2
        dialogActorTextView3.text = answerQuizData.actor3

        dialogSongTitleTextView.text = answerQuizData.songTitle
        dialogArtistTitleTextView.text = answerQuizData.artist

        alertDialog.setView(dialogView)
        alertDialog.show()
    }

    fun checkAnswer(){
        var title = binding.songTitleTextView.text.toString()
        var artist = binding.artistTextView.text.toString()
        fun String.removeWhitespaces() = replace(" ", "")

        var isCorrect: Boolean = (currentQuizData.songTitle).removeWhitespaces() == title.removeWhitespaces()
                && (currentQuizData.artist).removeWhitespaces() == artist.removeWhitespaces()

        // 오답인 경우
        if(!isCorrect){
            dialogAnswerTextView.text = "틀렸습니다."
            dialogAnswerTextView.setTextColor(Color.parseColor("#B00E23"))
            viewModel.updateHeartNumber()
            Log.d("QF_HEART_NUM",viewModel.heartNumber.toString())
            binding.heartImageView.setImageResource(setHeartImageFromHeartNumber(viewModel.heartNumber))
        }
        else { // 정답인 경우
            dialogAnswerTextView.text = "정답입니다."
            dialogAnswerTextView.setTextColor(Color.parseColor("#0056b9"))
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

