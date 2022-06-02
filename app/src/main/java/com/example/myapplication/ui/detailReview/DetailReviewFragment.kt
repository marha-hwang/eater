package com.example.myapplication.ui.detailReview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.api.ReviewsData
import com.example.myapplication.databinding.DetailReviewFragmentBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class DetailReviewFragment : Fragment(), list_onClick_interface {
    private var commentID: String = "" // 대댓글을 남길때 댓글의 ID얻어오기 위한 변수
    private var _binding: DetailReviewFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val DetailReviewViewModel =
            ViewModelProvider(this).get(DetailReviewViewModel::class.java)

        _binding = DetailReviewFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.reviewDelete.isVisible = false
        binding.imageView8.isVisible = false
        (activity as MainActivity).bottomNavigationShow(false)//bottom네비게이션 없애기

        binding.replyButton.isVisible = false // 대댓글 달기 버튼 숨기기
        val adapter = DetailReviewAdapter( this,DetailReviewViewModel,this, requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val args by navArgs<DetailReviewFragmentArgs>()
        val reviewID = args.documentID

        //해당리뷰내용 불러오기
        LoadReview(reviewID)
        //해당리뷰의 댓글 불러오기
        DetailReviewViewModel.LoadComment(reviewID)

        DetailReviewViewModel.CommentList.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
            adapter.setData(it)
        }
        val storageReference = Firebase.storage.reference.child(reviewID).child("food.jpg")
        CoroutineScope(Dispatchers.Default).launch {
            storageReference.downloadUrl.addOnSuccessListener {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.imageView8.isVisible = true
                    Glide.with(requireContext()).load(it).into(binding.imageView8)
                }
            }
        }

        //댓글 남기기
        binding.commentBtn.setOnClickListener{
            //로그인한 사용자만 댓글 남기기 가능
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(error.toString(), "사용자 정보 요청 실패")
                    Toast.makeText(requireContext(), "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    // 카카오 id
                    val userid = user?.id.toString()

                    val db: FirebaseFirestore = Firebase.firestore
                    val itemsCollectionRef = db.collection("Comment")

                    val itemMap = hashMapOf(
                        "reviewID" to reviewID,
                        "userID" to userid,
                        "comment" to binding.CommentText.text.toString(),
                        "date" to FieldValue.serverTimestamp(),
                        "likes" to 0,
                        "LikeUsers" to arrayListOf<String>()
                    )
                    itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                        .addOnSuccessListener {
                            binding.CommentText.text = null
                            (activity as MainActivity).dismissKeyboard()
                            // 댓글 갱신
                            DetailReviewViewModel.LoadComment(reviewID)

                        }
                        .addOnFailureListener {  }


                }
            }
        }
        // 대댓글 남기기
        binding.replyButton.setOnClickListener {
            //로그인한 사용자만 댓글 남기기 가능
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(error.toString(), "사용자 정보 요청 실패")
                    Toast.makeText(requireContext(), "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    // 카카오 id
                    val userid = user?.id.toString()

                    val db: FirebaseFirestore = Firebase.firestore
                    val itemsCollectionRef = db.collection("Reply")

                    val itemMap = hashMapOf(
                        "reviewID" to reviewID,
                        "commentID" to commentID,
                        "userID" to userid,
                        "comment" to binding.CommentText.text.toString(),
                        "date" to FieldValue.serverTimestamp(),
                    )
                    itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                        .addOnSuccessListener {
                            binding.CommentText.text = null
                            (activity as MainActivity).dismissKeyboard()
                            // 댓글 갱신
                            DetailReviewViewModel.LoadComment(reviewID)
                            binding.replyButton.isVisible = false

                        }
                        .addOnFailureListener {  }


                }
            }
        }

        binding.upBtn.setOnClickListener {
            val db: FirebaseFirestore = Firebase.firestore
            val itemsCollectionRef = db.collection("Reviews")

            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(error.toString(), "사용자 정보 요청 실패")
                    Toast.makeText(requireContext(), "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    itemsCollectionRef.whereEqualTo(FieldPath.documentId(), reviewID).get()
                        .addOnSuccessListener{
                            var userList: ArrayList<String> = it.documents.get(0).get("LikeUsers") as ArrayList<String>
                            if (!userList.contains(user?.id.toString())) {
                                Log.d("", it.documents.get(0).id)
                                itemsCollectionRef.document(it.documents.get(0).id)
                                    .update("likes", FieldValue.increment(1))
                                itemsCollectionRef.document(it.documents.get(0).id)
                                    .update("LikeUsers", FieldValue.arrayUnion(user?.id.toString())
                                    )
                                Toast.makeText(requireContext(), "추천 완료!", Toast.LENGTH_SHORT).show()

                            }else {
                                Toast.makeText(requireContext(), "이미 좋아요를 눌렀습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {

                        }
                }
            }
        }

        //리뷰삭제
        binding.reviewDelete.setOnClickListener {
            val db: FirebaseFirestore = Firebase.firestore
            val itemsCollectionRef = db.collection("Reviews")
            itemsCollectionRef.document(reviewID)
                .delete()
                .addOnSuccessListener {
                    val navController = findNavController()
                    navController.navigateUp()
                }
                .addOnFailureListener { Log.d("review삭제", "DocumentSnapshot successfully deleted!") }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).bottomNavigationShow(true)
    }

    // 해당리뷰의 내용을 불러오는 함수
    fun LoadReview(reviewID: String){
        var review: ReviewsData
        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Reviews").whereEqualTo(FieldPath.documentId(), reviewID)

        itemsCollectionRef.get().addOnSuccessListener {
           review = ReviewsData(
               it.documents.get(0).id,
               it.documents.get(0).get("restaurant_address").toString(),
               it.documents.get(0).get("restaurant_name").toString(),
               it.documents.get(0).get("title").toString(),
               it.documents.get(0).get("rating").toString(),
               it.documents.get(0).get("content").toString(),
               cnovertDateToTimestamp( it.documents.get(0).get("date") as Timestamp),
               it.documents.get(0).get("writer").toString()
            )
            //리뷰작성자와 현재유저가 일치할 경우 삭제버튼 표시
            UserApiClient.instance.me{ user, error ->
                if (error == null) {
                    binding.reviewDelete.isVisible = user?.id!!.toString().equals(review.writer)
                }
            }
            binding.Title.text = review.title
            binding.user.text = review.writer
            binding.date.text = review.date
            binding.content.text = review.content

        }.addOnFailureListener { exception ->
            Log.d("리뷰오류", "리뷰 불러오기오류")
        }
    }

    fun  cnovertDateToTimestamp(timestamp: Timestamp): String{
        val timeStampToDate = timestamp.toDate()
        val simpleDateFormat = SimpleDateFormat("MM-dd/HH시-mm분")
        val date = simpleDateFormat.format(timeStampToDate)
        return date.toString()
    }
    // 어탭터에서 대댓글달기 버튼 클릭시 기능 수행
    override fun onClick(commentID: String, boolean: Boolean) {
        binding.replyButton.isVisible = boolean
        this.commentID = commentID
        Log.d("commentID", "commentID" + commentID)
    }

}