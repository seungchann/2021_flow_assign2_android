package com.example.assign2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.assign2.RetrofitService.Companion.retrofitService
import com.example.assign2.databinding.ActivityMainBinding
import com.kakao.sdk.auth.model.OAuthToken
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    private val TAG = "StartActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var mCallback: (OAuthToken?, Throwable?) -> Unit

    private val retrofitService = RetrofitService.getInstance()
    lateinit var viewModel: MainViewModel
    lateinit var currentUser: Member

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        currentUser = intent.getSerializableExtra("currentUser") as Member

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        viewModel.currentUser = currentUser
        viewModel.errorMessage.observe(this, Observer { })
        viewModel.getAllQuizDatas()

        Log.d(TAG, currentUser.nickName)

        gameStartBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayout, QuizFragment())
                .commit()
        }
    }
}