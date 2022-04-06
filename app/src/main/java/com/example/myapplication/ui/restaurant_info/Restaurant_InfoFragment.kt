package com.example.myapplication.ui.restaurant_info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.RestaurantInfoFragmentBinding

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

        val args by navArgs<Restaurant_InfoFragmentArgs>()
        binding.restaurantAddress.text = args.restaurantAddress
        binding.restaurantName.text = args.restaurantName

        return root
    }






}