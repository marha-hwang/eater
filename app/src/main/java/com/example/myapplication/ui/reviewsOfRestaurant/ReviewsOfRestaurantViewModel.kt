package com.example.myapplication.ui.reviewsOfRestaurant

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.api.ReviewsData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewsOfRestaurantViewModel : ViewModel() {

    val ReviewList = MutableLiveData<ArrayList<ReviewsData>>()
    val Reviews = ArrayList<ReviewsData>()

    init{

    }

    fun LoadReviews(restaurant_name:String, restaurant_address:String){
        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Reviews")
            .whereEqualTo("restaurant_name",restaurant_name)

        Reviews.clear()

        itemsCollectionRef.get().addOnSuccessListener { documents ->
            for(document in documents) {
                //이름만 같은 음식점 걸러내기
                if (document.get("restaurant_address").toString().equals(restaurant_address)) {
                    Reviews.add(
                        ReviewsData(
                            document.id,
                            document.get("restaurant_address").toString(),
                            document.get("restaurant_name").toString(),
                            document.get("title").toString(),
                            document.get("rating").toString(),
                            document.get("content").toString(),
                            cnovertDateToTimestamp(document.get("date") as Timestamp),
                            document.get("writer").toString()
                        )
                    )
                    Log.d("timestamp", "timestamp" + document.get("date").toString())
                }
            }
            Reviews.sortByDescending { it.date } //시간순에따라 받아온 리뷰리스트 정렬하기
            ReviewList.value = Reviews
            Log.d("review목록", Reviews.size.toString())
        }.addOnFailureListener { exception ->
            Log.d("리뷰오류", "리뷰 불러오기오류")
        }
    }

    fun  cnovertDateToTimestamp(timestamp: Timestamp): String{
        val timeStampToDate = timestamp.toDate()
        val simpleDateFormat = SimpleDateFormat("MM-dd/HH시-mm분-ss초")
        val date = simpleDateFormat.format(timeStampToDate)
        return date.toString()
    }
}
