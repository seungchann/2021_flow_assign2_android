package com.example.assign2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.assign2.databinding.ActivityMainBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.android.synthetic.main.activity_main.*
import com.example.assign2.QuizFragment as QuizFragment

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var mCallback: (OAuthToken?, Throwable?) -> Unit

    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        viewModel.errorMessage.observe(this, Observer { })
        viewModel.getAllQuizDatas()
        makeKakaoCallback()
        checkKakaoLogin()

        myButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayout, QuizFragment())
                .addToBackStack(".MainActivity")
                .commit()
        }
    }

    fun makeKakaoCallback() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) { Log.e(TAG, "로그인 실패", error) }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) { Log.e(TAG, "사용자 정보 요청 실패", error) }
                    else if (user != null) { viewModel.makeMemberFromKakaoDB(user) }
                }
            }
        }
        mCallback = callback
    }

    fun checkKakaoLogin() {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError() == true) { KakaoLogin() }
                    else { Log.e("Kakao_Token", "기타 에러") } //기타 에러
                }
                else {
                    Log.e("토큰이","유효합니다.")
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) { Log.e(TAG, "사용자 정보 요청 실패", error) }
                        else if (user != null) { viewModel.makeMemberFromKakaoDB(user) }
                    }
                }
            }
        }
        else { KakaoLogin() }
    }

    // 카카오톡이 설치되어 있을 경우, 카카오톡 이용
    // 그렇지 않을 경우, 카카오 계정으로 로그인
    fun KakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = mCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
        }
    }
}