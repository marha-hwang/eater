package com.example.myapplication.ui.detailReview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.api.CommentData
import com.example.myapplication.api.ReplyData
import com.example.myapplication.api.ReviewsData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class DetailReviewViewModel : ViewModel() {

    val CommentList = MutableLiveData<ArrayList<CommentData>>()
    val Comments = ArrayList<CommentData>()

    val ReplyList = MutableLiveData<ArrayList<ReplyData>>()
    val Replys = ArrayList<ReplyData>()

    init{

    }

    //댓글을 불러오기 위한 함수
    fun LoadComment(reviewID: String){
        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Comment").whereEqualTo("reviewID", reviewID)

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
            LoadReply(reviewID)
            Log.d("review목록", Comments.toString())
        }.addOnFailureListener { exception ->
            Log.d("댓글오류", "댓글 불러오기오류")
        }
    }

    //대대대댓댓글을 불러오기 위한 함수
    fun LoadReply(reviewID: String){
        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Reply").whereEqualTo("reviewID", reviewID)

        Replys.clear()
        itemsCollectionRef.get().addOnSuccessListener { documents ->
            for(document in documents){
                Replys.add(
                    ReplyData(
                        document.id,
                        document.get("reviewID").toString(),
                        document.get("commentID").toString(),
                        document.get("userID").toString(),
                        document.get("comment").toString(),
                        cnovertDateToTimestamp(document.get("date") as Timestamp),
                    )
                )
                Log.d("timestamp", "timestamp" + document.get("date").toString())
            }
            Replys.sortByDescending { it.date } //시간순에따라 받아온 리뷰리스트 정렬하기
            ReplyList.value = Replys
            Log.d("review목록", Replys.toString())
        }.addOnFailureListener { exception ->
            Log.d("댓글오류", "댓글 불러오기오류")
        }
    }

    fun  cnovertDateToTimestamp(timestamp: Timestamp): String{
        val timeStampToDate = timestamp.toDate()
        val simpleDateFormat = SimpleDateFormat("MM-dd/HH시-mm분")
        val date = simpleDateFormat.format(timeStampToDate)
        return date.toString()
    }

}