package com.example.myapplication.ui.detailReview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.CommentData
import com.example.myapplication.api.ReplyData
import com.example.myapplication.databinding.ItemCommentBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient

class DetailReviewAdapter(var lifecycleOwner: LifecycleOwner, var viewModel: DetailReviewViewModel, var listOnclickInterface: list_onClick_interface, private val context: Context) : RecyclerView.Adapter<DetailReviewAdapter.ViewHolder>(){

    var CommentList = mutableListOf<CommentData>()
    val db: FirebaseFirestore = Firebase.firestore
    val itemsCollectionRef = db.collection("Comment")

    inner class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(reviewID: String, commentID: String, userID: String, comment: String, date: String){

            //중첩리사이클러뷰를 위한 코드
            val adapter = ReplyAdapter(viewModel)
            var layoutSize = 0
            binding.recyclerView2.adapter = adapter
            binding.recyclerView2.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            viewModel.ReplyList.observe(lifecycleOwner) {
                val replys = ArrayList<ReplyData>()
                adapter.notifyDataSetChanged()
                for(reply in it) {
                    if(reply.CommentID.equals(commentID)) {
                        replys.add(
                            ReplyData(
                                reply.replyID,
                                reply.reviewID,
                                reply.CommentID,
                                reply.userID,
                                reply.comment,
                                reply.date,
                            )
                        )
                        layoutSize+=1
                    }
                }
                binding.recyclerView2.layoutParams.height = 200*layoutSize //대댓글을 위한 리사이클러뷰의 크기 조절을 동적으로 한다.
                adapter.setData(replys)
            }

            binding.commentDelete.isVisible = false
            binding.commentUser.text = userID
            binding.comment.text = comment
            binding.commentDate.text = date
            binding.replyBtn.setOnClickListener {
                if(binding.replyBtn.text.equals("답글달기")) {
                    binding.replyBtn.text = "취소"
                    listOnclickInterface.onClick(commentID,true)

                } else if (binding.replyBtn.text.equals("취소")){
                    binding.replyBtn.text = "답글달기"
                    listOnclickInterface.onClick("",false)
                }
            }
            //해당 댓글이 현재유저가 작성한 댓글인 경우 삭제버튼표시 아니면 미표시
            UserApiClient.instance.me{ user, error ->
                if (error == null) {
                    binding.commentDelete.isVisible = user?.id!!.toString().equals(userID)
                }
            }
            //삭제버튼 기능구현
            binding.commentDelete.setOnClickListener {
                itemsCollectionRef.document(commentID)
                    .delete()
                    .addOnSuccessListener {
                        viewModel.LoadComment(reviewID)
                        Log.d("comment삭제", "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { Log.d("comment삭제", "DocumentSnapshot successfully deleted!") }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailReviewAdapter.ViewHolder {
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