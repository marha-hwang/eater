package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.restaurant_info.Restaurant_InfoFragmentArgs

import kotlin.RuntimeException

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var search_keyword = "한성대"  //음식점 리스트를 검색할 변수, 위치정보 기능으로 자동으로 키워드가 입력되어야함

        //search 프래그먼트에서 검색어 입력을 통해 음식점 리스트 출력하는 코드
        try {
            val args by navArgs<HomeFragmentArgs>()
            search_keyword = args?.searchInput.toString()
            homeViewModel.searchKeyword(search_keyword,"FD6")
            Log.d("search Input", args?.searchInput.toString())
        } catch(e: java.lang.reflect.InvocationTargetException){
            homeViewModel.searchKeyword(search_keyword,"FD6")
            Log.d("no search Input"," 검색어가 없음")
        }

        val adapter = HomeRecyclerViewAdapter(HomeViewModel(),this.requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(activity,2) // 가로정렬
        //binding.recyclerView.layoutManager = LinearLayoutManager(activity) //세로정렬

        homeViewModel.RestaurantList.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
            adapter.setData(it)
        }

       /* binding.homeSearchbutton.setOnClickListener {
            homeViewModel.searchKeyword(binding.homeSearchfield.text.toString(),"FD6")
        }*/

        //툴바 커스텀
        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)
        binding.toolbar.setTitle(null) //타이틀 없애기
        binding.imageButton3.setOnClickListener{
            val action= HomeFragmentDirections.actionNavigationHomeToSearchFragment()
            navController?.navigate(action)
        }



        return root
    }


}
