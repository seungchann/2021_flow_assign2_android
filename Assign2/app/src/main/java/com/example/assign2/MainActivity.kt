package com.example.assign2

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var mCallback: (OAuthToken?, Throwable?) -> Unit

    lateinit var currentUser: Member

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeKakaoCallback()

        kakaoButton.setOnClickListener {
            checkKakaoLogin()
        }

        myButton.setOnClickListener {

        }
    }

    fun makeKakaoCallback() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) { Log.e(TAG, "로그인 실패", error) }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) { Log.e(TAG, "사용자 정보 요청 실패", error) }
                    else if (user != null) {
                        makeMemberFromKakaoDB(user)
                        moveToStartActivity()
                    }
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
                        else if (user != null) {
                            makeMemberFromKakaoDB(user)
                            moveToStartActivity()
                        }
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

    fun makeMemberFromKakaoDB(user: User) {
        Log.i(TAG, "사용자 정보 요청 성공" +
                "\n회원번호: ${user.id}" +
                "\n이메일: ${user.kakaoAccount?.email}" +
                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

        val newMember = Member(user.kakaoAccount?.profile?.nickname ?: "default",
            user.kakaoAccount?.email ?: "@.",
            user.kakaoAccount?.profile?.thumbnailImageUrl ?: "",
            0)

        currentUser = newMember
    }

    fun moveToStartActivity() {
        val nextIntent = Intent(this, StartActivity::class.java)
        nextIntent.putExtra("currentUser",currentUser)
        startActivity(nextIntent)
    }
}