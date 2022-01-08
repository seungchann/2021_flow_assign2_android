package com.example.assign2

import com.google.gson.annotations.SerializedName


data class QuizData(
    @SerializedName("id")
    var id: Integer,
    @SerializedName("song_title")
    var songTitle: String,
    @SerializedName("artist")
    var artist: String,
    @SerializedName("video_title")
    var video_title: String,
    @SerializedName("date")
    var date: Integer,
    @SerializedName("actor1")
    var actor1: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("dialogue")
    var dialogue: String,
)

data class Member(
    @SerializedName("nickname")
    var nickName: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("profile_url")
    var profileURL: String,
    @SerializedName("highest_score")
    var highestScore: Int,
)
