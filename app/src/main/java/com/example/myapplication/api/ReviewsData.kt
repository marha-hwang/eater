package com.example.myapplication.api

import java.text.SimpleDateFormat
import java.util.*

data class ReviewsData(
    val documentId: String,
    val restaurant_address: String,
    val restaurant_name: String,
    val title: String,
    val rating: String,
    val content: String,
    val date: String,
    val writer: String
)

data class CommentData(
    val reviewID: String,
    val commentID: String,
    val userID: String,
    val comment: String,
    val date: String
)

data class ReplyData(
    val replyID: String,
    val reviewID: String,
    val CommentID: String,
    val userID : String,
    val comment: String,
    val date: String

)
