package com.example.myapplication.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.api.ReviewsData
import com.example.myapplication.databinding.ItemReviewBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyreviewsAdapter(val context: Context) : RecyclerView.Adapter<MyreviewsAdapter.ViewHolder>(){

    var reviewList = mutableListOf<ReviewsData>()

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(documentId: String, title: String, content: String, date: String) {
            val storageReference = Firebase.storage.reference.child(documentId).child("food.jpg")

            binding.imageView3.isVisible = false
            binding.NotificationTitle.text = title
            binding.NotificationContent.text = content
            binding.NotificationDate.text = date

            binding.cardView.setOnClickListener{
                val nav = Navigation.findNavController(it)
                val action = MypageFragmentDirections.actionNavigationMypageToDetailReviewFragment(documentId)
                nav?.navigate(action)
            }

            CoroutineScope(Dispatchers.Default).launch {
                storageReference.downloadUrl.addOnSuccessListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.imageView3.isVisible = true
                        Glide.with(context).load(it).into(binding.imageView3)
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyreviewsAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyreviewsAdapter.ViewHolder, position: Int) {
        holder.bind(
            reviewList[position].documentId,
            reviewList[position].title,
            reviewList[position].content,
            reviewList[position].date
        )
    }

    override fun getItemCount(): Int {
        return if(reviewList == null) 0
        else reviewList.size
    }

    fun setData(data : ArrayList<ReviewsData>){
        reviewList = data
    }
}