package com.example.myapplication.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.CommentData
import com.example.myapplication.databinding.ItemCommentBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MycommentAdapter(val context: Context) : RecyclerView.Adapter<MycommentAdapter.ViewHolder>() {

    var CommentList = mutableListOf<CommentData>()

    inner class ViewHolder(private val binding: ItemCommentBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun bind(reviewID: String, commentID: String, userID: String, comment: String, date: String
                ) {
                    binding.commentDelete.isVisible = false
                    binding.replyBtn.isVisible = false
                    binding.commentUser.text = userID
                    binding.comment.text = comment
                    binding.commentDate.text = date

                    binding.comment.setOnClickListener{
                        val nav = Navigation.findNavController(it)
                        val db: FirebaseFirestore = Firebase.firestore
                        val itemsCollectionRef = db.collection("Reviews").whereEqualTo(FieldPath.documentId(), reviewID)
                        itemsCollectionRef.get().addOnSuccessListener {
                            if(it.isEmpty){
                                Toast.makeText(context, "삭제된 리뷰글입니다", Toast.LENGTH_SHORT).show()
                            } else {
                                val action = MypageFragmentDirections.actionNavigationMypageToDetailReviewFragment(reviewID)
                                nav?.navigate(action)
                            }
                        }
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            CommentList[position].reviewID,
            CommentList[position].commentID,
            CommentList[position].userID,
            CommentList[position].comment,
            CommentList[position].date,
        )
    }

    override fun getItemCount(): Int {
        return if(CommentList == null) 0
        else CommentList.size
    }

    fun setData(data : ArrayList<CommentData>){
        CommentList = data
    }
}