package com.example.myapplication.ui.detailReview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.CommentData
import com.example.myapplication.api.ReplyData
import com.example.myapplication.databinding.ItemCommentBinding
import com.example.myapplication.databinding.ItemReplyBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient

class ReplyAdapter(var viewModel: DetailReviewViewModel) : RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    var replyList = mutableListOf<ReplyData>()
    val db: FirebaseFirestore = Firebase.firestore
    val itemsCollectionRef = db.collection("Reply")

    inner class ViewHolder(private val binding: ItemReplyBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(reviewID: String, userID: String, reply: String, date: String, replyID: String){
                binding.replyDelete.isVisible = false
                binding.userID.text = userID
                binding.reply.text = reply
                binding.time.text = date
                UserApiClient.instance.me{ user, error ->
                    if (error == null) {
                        binding.replyDelete.isVisible = user?.id!!.toString().equals(userID)
                    }
                }
                binding.replyDelete.setOnClickListener {
                    itemsCollectionRef.document(replyID)
                        .delete()
                        .addOnSuccessListener {
                            viewModel.LoadComment(reviewID)
                            Log.d("reply삭제", "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener { Log.d("reply삭제", "DocumentSnapshot successfully deleted!") }
                }
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReplyBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            replyList[position].reviewID,
            replyList[position].userID,
            replyList[position].comment,
            replyList[position].date,
            replyList[position].replyID
        )
    }

    override fun getItemCount(): Int {
        return if(replyList == null) 0
        else replyList.size
    }

    fun setData(data: ArrayList<ReplyData>){
        replyList = data
    }
}