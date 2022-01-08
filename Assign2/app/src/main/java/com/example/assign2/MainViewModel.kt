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

    val QuizDataList = MutableLiveData<List<QuizData>>()
    val errorMessage = MutableLiveData<String>()

    fun getAllQuizDatas() {
        val response = repository.getAllQuizDatas()
        response.enqueue(object : Callback<List<QuizData>> {
            override fun onResponse(call: Call<List<QuizData>>, response: Response<List<QuizData>>) {
                QuizDataList.postValue(response.body())
            }

            override fun onFailure(call: Call<List<QuizData>>, t: Throwable) {
                errorMessage.postValue(t.message)
                Log.d(TAG, "response error, message : ${t.message}")
            }
        })
    }

}