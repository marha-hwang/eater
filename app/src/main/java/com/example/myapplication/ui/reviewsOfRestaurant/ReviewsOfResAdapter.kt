package com.example.myapplication.ui.reviewsOfRestaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.ReviewsData
import com.example.myapplication.databinding.ItemReviewBinding

class ReviewsOfResAdapter : RecyclerView.Adapter<ReviewsOfResAdapter.ViewHolder>(){

    var reviewList = mutableListOf<ReviewsData>()

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(title: String, content: String, date: String){
            binding.NotificationTitle.text = title
            binding.NotificationContent.text = content
            binding.NotificationDate.text = date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reviewList[position].title,
            reviewList[position].content,
            reviewList[position].date)
    }

    override fun getItemCount(): Int {
        return if(reviewList == null) 0
        else reviewList.size
    }

    fun setData(data : ArrayList<ReviewsData>){
        reviewList = data
    }
}