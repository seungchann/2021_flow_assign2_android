package com.example.assign2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel constructor(private val repository: MainRepository) : ViewModel() {

    private val TAG = "MainViewModel"

    var QuizDataList = mutableListOf<QuizData>()
    val errorMessage = MutableLiveData<String>()
    lateinit var currentUser: Member
    var heartNumber: Int = 5
    var hintNumber: Int = 5
    var correctNumber: Int = 0 // 맞힌 개수

    fun getAllQuizDatas() {
        val response = repository.getAllQuizDatas()
        response.enqueue(object : Callback<List<QuizData>> {
            override fun onResponse(call: Call<List<QuizData>>, response: Response<List<QuizData>>) {
                QuizDataList = response.body()?.toMutableList()!!
            }

            override fun onFailure(call: Call<List<QuizData>>, t: Throwable) {
                errorMessage.postValue(t.message)
                Log.d(TAG, "response error, message : ${t.message}")
            }
        })
    }

    fun updateHeartNumber() {
        if (this.heartNumber < 0) {
            return
        } else {
            this.heartNumber = this.heartNumber - 1
            Log.d(TAG,"HeartNumber: ${this.heartNumber}")
            return
        }
    }

    fun updateHintNumber(num: Int) {
        if (this.hintNumber < 0) {
            return
        } else {
            this.hintNumber = this.hintNumber - num
            Log.d(TAG, "HintnUmber: ${this.hintNumber}")
            return
        }
    }

    fun isGameOver(): Boolean {
        return this.heartNumber == 0
    }

    fun updateCorrectNumber() {
        // 추가 : DB한테 총 문제 개수 받아와서 총 문제 개수보다 맞은 개수가 크지 않도록 return 해줘야한다
        if (this.correctNumber < 0) {
            return
        } else {
            this.correctNumber = this.correctNumber + 1
            return
        }
    }

}