package com.example.assign2

class MainRepository constructor(private val retrofitService: RetrofitService) {

    fun getAllQuizDatas() = retrofitService.getAllQuizDatas()
    fun getAllMembers() = retrofitService.getAllMembers()
    fun addMember(member: Member) = retrofitService.addMember(member)
}