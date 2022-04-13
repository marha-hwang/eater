package com.example.myapplication.ui.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding.inflate
import com.example.myapplication.databinding.SearchFragmentBinding
import com.example.myapplication.ui.home.HomeFragmentDirections

class SearchFragment : Fragment() {
    private  var _binding: SearchFragmentBinding? = null
    private  val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater,container, false)
        val root: View = binding.root

        //툴바 커스텀
        val nav = findNavController()
        binding.toolbar.setupWithNavController(nav)
        binding.toolbar.setTitle(null) //타이틀 없애기

        //home 프래그먼트로 검색어 전달
        binding.imageButton.setOnClickListener {
            val search_input = binding.searchInputText.text.toString()
            val action = SearchFragmentDirections.actionSearchFragmentToNavigationHome(search_input)
            nav?.navigate(action)
        }
        return root
    }
}