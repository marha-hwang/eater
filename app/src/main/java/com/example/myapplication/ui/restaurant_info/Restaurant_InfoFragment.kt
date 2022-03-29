package com.example.myapplication.ui.restaurant_info

import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.databinding.RestaurantInfoFragmentBinding
import com.example.myapplication.ui.home.HomeFragment
import com.example.myapplication.ui.mypage.MypageFragment
import com.example.myapplication.ui.notifications.NotificationsViewModel

class Restaurant_InfoFragment : Fragment() {


    private var _binding: RestaurantInfoFragmentBinding? = null
    private lateinit var viewModel: RestaurantInfoViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = RestaurantInfoFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }


}