package com.example.myapplication.ui.WriteReview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.databinding.FragmentReviewBinding
import com.example.myapplication.ui.restaurant_info.Restaurant_InfoFragmentArgs
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

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
                "writer" to "",
            )
            itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                .addOnSuccessListener { navController.navigateUp() }
                .addOnFailureListener { }
            //itemsCollectionRef.document("docTest").set(itemMap) //기존 document에 덮어쓰기
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}