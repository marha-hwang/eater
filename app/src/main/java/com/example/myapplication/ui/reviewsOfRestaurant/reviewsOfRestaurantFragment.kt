package com.example.myapplication.ui.reviewsOfRestaurant

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.databinding.ReviewsOfRestaurantFragmentBinding
import com.example.myapplication.ui.notifications.NotificationsAdapter
import com.example.myapplication.ui.notifications.NotificationsViewModel
import com.example.myapplication.ui.restaurant_info.Restaurant_InfoFragmentArgs

class reviewsOfRestaurantFragment : Fragment() {

    private var _binding: ReviewsOfRestaurantFragmentBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val reviewsOfrestaurantViewModel =
            ViewModelProvider(this).get(ReviewsOfRestaurantViewModel::class.java)

        _binding = ReviewsOfRestaurantFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val args by navArgs<reviewsOfRestaurantFragmentArgs>()
        val restaurant_name = args.restaurantName
        val restaurant_address = args.restaurantAddress

        val adapter = NotificationsAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        reviewsOfrestaurantViewModel.LoadReviews(restaurant_name, restaurant_address)

        reviewsOfrestaurantViewModel.ReviewList.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
            adapter.setData(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}