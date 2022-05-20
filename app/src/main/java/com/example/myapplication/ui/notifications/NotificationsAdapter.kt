package com.example.myapplication.ui.notifications

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.ReviewsData
import com.example.myapplication.databinding.ItemReviewBinding
import com.example.myapplication.ui.home.HomeFragmentDirections
import kotlin.collections.ArrayList

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>(){

    var reviewList = mutableListOf<ReviewsData>()

    inner class ViewHolder(private val binding: ItemReviewBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun bind(ID: String, title: String, content: String, date: String){
                    binding.NotificationTitle.text = title
                    binding.NotificationContent.text = content
                    binding.NotificationDate.text = date
                    binding.cardView.setOnClickListener{
                        val nav = Navigation.findNavController(it)
                        val action= NotificationsFragmentDirections.actionNavigationNotificationsToDetailReviewFragment(ID)
                        nav?.navigate(action)
                        Log.d("id", "id= " + ID)
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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