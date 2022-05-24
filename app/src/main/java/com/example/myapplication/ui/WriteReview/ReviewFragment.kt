package com.example.myapplication.ui.WriteReview

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.databinding.FragmentReviewBinding
import com.example.myapplication.ui.restaurant_info.Restaurant_InfoFragmentArgs
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.user.UserApiClient
import java.io.ByteArrayOutputStream


class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private var userid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //파이어베이스 사용
        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Reviews")

        val args by navArgs<ReviewFragmentArgs>()
        val restaurant_name = args.restaurantName
        val restaurant_address = args.restaurantAddress

        Log.d("restaurant name", restaurant_name)
        Log.d("restaurant address", restaurant_address)

        val navController = findNavController()

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(error.toString(), "사용자 정보 요청 실패")
            }
            else {
                // 카카오 id
                userid = user?.id.toString()
            }
        }

        binding.confirmButton.setOnClickListener {
            val rating= binding.ratingBar.rating.toDouble()
            val title = binding.reviewTitle.text.toString()
            val content = binding.reviewText.text.toString()

            val itemMap = hashMapOf(
                "restaurant_address" to restaurant_address,
                "restaurant_name" to restaurant_name,
                "title" to title,
                "rating" to rating,
                "content" to content,
                "date" to FieldValue.serverTimestamp(),
                "writer" to userid,
                "likes" to 0,
                "LikeUsers" to arrayListOf<String>()
            )
            itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                .addOnSuccessListener {
                    try{
                        //첨부된 사진 형식변환
                        val bitmap = (binding.foodImage.drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        //파이어베이스에 올리기
                        var storageRef = Firebase.storage.reference
                        val imageRef = storageRef.child(it.id).child("food.jpg")
                        var uploadTask = imageRef.putBytes(data)
                        uploadTask.addOnFailureListener {
                            Log.d("이미지", "이미지첨부실패")
                        }
                            .addOnSuccessListener {
                                Log.d("이미지", "이미지첨부성공")
                            }
                    }catch(e:java.lang.ClassCastException){ "이미지 첨부안함"}

                    navController.navigateUp()
                }
                .addOnFailureListener { }
            //itemsCollectionRef.document("docTest").set(itemMap) //기존 document에 덮어쓰기
        }

        //갤러리로 이동하여 사진을 가져옴
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
            //갤러리에서 사진을 가져온후 수행할 작업정의
            binding.foodImage.setImageURI(uri)
        }
        binding.addPicture.setOnClickListener {
            if((activity as MainActivity).checkPermissionForAlbum(requireContext())){
                Log.d("이미지", "이미지첨부")
                getContent.launch("image/*")
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigationShow(false)//bottom네비게이션 없애기
    }

}