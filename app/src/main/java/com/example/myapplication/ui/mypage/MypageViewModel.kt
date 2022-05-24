package com.example.myapplication.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.api.CommentData
import com.example.myapplication.api.LikesData
import com.example.myapplication.api.ReviewsData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class MypageViewModel : ViewModel() {

    val LikeList = MutableLiveData<ArrayList<LikesData>>()
    val Likes = ArrayList<LikesData>()

    val ReviewList = MutableLiveData<ArrayList<ReviewsData>>()
    val Reviews = ArrayList<ReviewsData>()

    val CommentList = MutableLiveData<ArrayList<CommentData>>()
    val Comments = ArrayList<CommentData>()

    val db: FirebaseFirestore = Firebase.firestore

    init {

    }

    fun LoadlikeLes(userID: String) {
        val itemsCollectionRef = db.collection("Restaurants").whereArrayContains("LikeUsers", userID)
        //LikeUsers라는 필드인 배열안에 해당인자가 존재하는 문서를 가져온다.
        Likes.clear()
        itemsCollectionRef.get().addOnSuccessListener { documents ->
            for(document in documents){
                Likes.add(
                    LikesData(
                        document.get("res_address").toString(),
                        document.get("res_name").toString(),
                        document.get("res_url").toString(),
                    )
                )
            }
            Log.d("likes", documents.size().toString())
            LikeList.value = Likes
        }.addOnFailureListener { exception ->
            Log.d("리뷰오류", "리뷰 불러오기오류")
        }

    }

    fun LoadReview(userID: String) {
        Log.d("review", userID)
        val itemCollectionRef = db.collection("Reviews").whereEqualTo("writer", userID)
        Reviews.clear()
        itemCollectionRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
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
            ReviewList.value = Reviews
            Log.d("review목록", Reviews.size.toString())
        }.addOnFailureListener { exception ->
            Log.d("리뷰오류", "리뷰 불러오기오류")
        }
    }

    fun LoadComment(userID: String) {
        val itemsCollectionRef = db.collection("Comment").whereEqualTo("userID", userID)
        Comments.clear()
        itemsCollectionRef.get().addOnSuccessListener { documents ->
            for(document in documents){
                Comments.add(
                    CommentData(
                        document.get("reviewID").toString(),
                        document.id.toString(),
                        document.get("userID").toString(),
                        document.get("comment").toString(),
                        cnovertDateToTimestamp(document.get("date") as Timestamp),
                    )
                )
                Log.d("timestamp", "timestamp" + document.get("date").toString())
            }
            Comments.sortByDescending { it.date } //시간순에따라 받아온 리뷰리스트 정렬하기
            CommentList.value = Comments
            Log.d("review목록", Comments.toString())
        }.addOnFailureListener { exception ->
            Log.d("댓글오류", "댓글 불러오기오류")
        }
    }

    fun cnovertDateToTimestamp(timestamp: Timestamp): String {
        val timeStampToDate = timestamp.toDate()
        val simpleDateFormat = SimpleDateFormat("MM-dd/HH시-mm분")
        val date = simpleDateFormat.format(timeStampToDate)
        return date.toString()
    }
}