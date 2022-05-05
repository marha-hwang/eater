package com.example.myapplication.api

import java.text.SimpleDateFormat
import java.util.*

data class ReviewsData(
    val restaurant_address: String,
    val restaurant_name: String,
    val title: String,
    val rating: String,
    val content: String,
    val date: String,
    val writer: String
)
