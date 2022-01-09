package com.example.assign2

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel constructor(private val repository: MainRepository) : ViewModel() {

    private val TAG = "LoginViewModel"

    val memberList = MutableLiveData<List<Member>>()
    val errorMessage = MutableLiveData<String>()

    fun getAllMembers() {
        val response = repository.getAllMembers()
        response.enqueue(object: Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                memberList.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                errorMessage.postValue(t.message)
                Log.d(TAG, "response error, message : ${t.message}")
            }
        })
    }

    fun addMemeber(member: Member) {
        val response = repository.addMember(member)
        response.enqueue(object: Callback<Member> {
            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                Log.d(TAG,"신규 유저가 등록되었습니다.")
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                Log.e(TAG,"onFailure.")
            }
        })
    }

}