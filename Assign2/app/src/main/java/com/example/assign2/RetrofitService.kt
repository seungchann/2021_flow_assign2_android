package com.example.assign2
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// 서버에서 호출할 메서드를 선언하는 인터페이스
// POST 방식으로 데이터를 주고 받을 때 넘기는 변수는 Field라고 해야한다.
interface RetrofitService {

    @GET("/QuizData/")
    fun getAllQuizDatas(): Call<List<QuizData>>

    @GET("/Member/")
    fun getAllMembers(): Call<List<Member>>

    @POST("/Member/")
    fun addMember(@Body member: Member): Call<Member>

    companion object {
        var retrofitService: RetrofitService? = null
        private val gson = GsonBuilder().setLenient().create()

        fun getInstance() : RetrofitService {

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.249.18.191:80/") // 서버 주소
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }

}