package com.example.assign2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "90dcedf27ec2fd0ac481ffe385e66a89")
    }
}