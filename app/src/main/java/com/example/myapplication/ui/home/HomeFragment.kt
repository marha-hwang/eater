package com.example.myapplication.ui.home

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.recommandMenu.model.Food
import com.google.android.gms.location.*
import com.kakao.sdk.user.UserApiClient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(),setURL_interface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var x: String = "126.978652258823"
    private var y: String = "37.56682420267543"

    //현재위치를 가져오기 위한 변수
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    //위치 값을 가지고 있는 객체
    lateinit var mLastLocation: Location

    //위치정보 요청의 매개변수를 저장하는 객체
    internal lateinit var mLocationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //mFusedLocationProviderClient 변수 초기화
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        //mLocationRequest 변수 초기화
        mLocationRequest = LocationRequest.create().apply {
            //interval = 2000 // 업데이트 간격 단위(밀리초)
            fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            maxWaitTime = 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        //시스템으로 부터 위치 정보를 콜백으로 받은 후 UI작업과 api작업을 수행하는 함수
        val mLocationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                mLastLocation = locationResult.lastLocation
                val date: Date = Calendar.getInstance().time
                val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")
                Log.d(
                    ContentValues.TAG,
                    simpleDateFormat.format(date) + "위도 " + mLastLocation.latitude + "경도 " + mLastLocation.longitude
                )

                // 전역변수 위도 경도값 저장
                x = mLastLocation.longitude.toString()
                y = mLastLocation.latitude.toString()

                homeViewModel.searchAddress(
                    x, y, "WGS84"
                )
            }
        }
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(error.toString(), "사용자 정보 요청 실패")
            } else {
                // 카카오 id
                val userId = user?.id.toString()
                Log.e(ContentValues.TAG, "로그인 ID ${userId}")
            }
        }
        //위치 업데이트를 하는 함수
        fun startLocationUpdates() {

            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(ContentValues.TAG, "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            }
            mFusedLocationProviderClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallBack,
                Looper.myLooper()!!
            )
        }
        //마지막 위치정보 얻어오기, 마지막 위치 정보가 없으면 defalt 값으로 서울 시청으로 설정
        if ((activity as MainActivity).checkPermissionForLocation(requireContext())) { //권한 요청
            mFusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                Log.d("location0", "location" + location)
                if ((location == null) || ((location!!.longitude >= 0) && (location!!.latitude >= 0))) {
                    if (location != null) {
                        x = location.longitude.toString()
                        y = location.latitude.toString()
                        homeViewModel.searchAddress(
                            x, y, "WGS84"
                        )
                        Log.d("location1", "location" + location!!.longitude.toString())
                    }
                    homeViewModel.searchAddress(x, y, "WGS84")
                    Log.d("location2", "location: " + location)
                } else {
                    homeViewModel.searchAddress(x, y, "WGS84")
                    Log.d("location3", "location")
                }
            }
        } else {
            homeViewModel.searchAddress(x, y, "WGS84")
            Log.d("location4", "location")
        }

        var search_keyword = ""
        //search 프래그먼트에서 검색어 입력을 통해 음식점 리스트 출력하는 코드
        try {
            val args by navArgs<HomeFragmentArgs>()
            var search_keyword = args?.searchInput.toString()
            homeViewModel.searchKeyword(search_keyword, "FD6")
            Log.d("search Input", args?.searchInput.toString())
        } catch (e: java.lang.reflect.InvocationTargetException) {
            CoroutineScope(Dispatchers.Default).launch {
                launch {
                    while (homeViewModel.region_2depth_name.value == null) {
                        sleep(1)
                    }
                    var search_keyword = homeViewModel.region_2depth_name.value.toString()
                    Log.d("coroutinTest", search_keyword)
                    //homeViewModel.searchKeyword(search_keyword, "FD6")
                    homeViewModel.searchcategory(x,y, "음식점 > ")
                    Log.d("no search Input", " 검색어가 없음")
                }
            }
        }

        // 음식점 카테고리 분류
        binding.allfood.isChecked = true
        binding.groupCategoryType.setOnClickedButtonListener { _, position ->
            when(position){
                0 -> homeViewModel.searchcategory(x,y, "음식점 > ")
                1 -> homeViewModel.searchcategory(x,y,"음식점 > 한식 , 음식점 > 치킨 , 음식점 > 분식 ")
                2 -> homeViewModel.searchcategory(x,y,"음식점 > 중식 ")
                3 -> homeViewModel.searchcategory(x,y,"음식점 > 일식 ")
                4 -> homeViewModel.searchcategory(x,y,"음식점 > 양식 ")
            }
        }

        //카드뷰 출력
        val adapter = HomeRecyclerViewAdapter(this, HomeViewModel(), this.requireContext())
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = GridLayoutManager(activity, 2) // 가로정렬
            //binding.recyclerView.layoutManager = LinearLayoutManager(activity) //세로정렬

            homeViewModel.RestaurantList.observe(viewLifecycleOwner) {
                adapter.notifyDataSetChanged()
                adapter.setData(it)
            }
        homeViewModel.address.observe(viewLifecycleOwner) {
            binding.textView8.text = homeViewModel.address.value.toString()
        }
        //툴바 커스텀
        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)
        binding.toolbar.setTitle(null) //타이틀 없애기
        binding.imageButton3.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToSearchFragment()
            navController?.navigate(action)
        }

        //위치정보를 가져오는 함수를 호출
        binding.gpsButton.setOnClickListener {
            if ((activity as MainActivity).checkPermissionForLocation(requireContext())) { //권한 요청
                mFusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                    Log.d("location0", "location" + location)
                    if ((location == null) || ((location!!.longitude >= 0) && (location!!.latitude >= 0))) {
                        if (location != null) {
                            x = location.longitude.toString()
                            y = location.latitude.toString()
                            Log.d("location1", x +" "+y)
                        }
                    }
                }
            }
            CoroutineScope(Dispatchers.Default).launch {
                launch {
                    sleep(200L)
                    homeViewModel.searchcategory(x,y,"음식점 > ")
                }
            }
        }

        binding.Recommandbtn.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToRecommandFragment()
            navController?.navigate(action)
        }
        return root
    }

    override fun setUrl(place_url: String, photo_url: String) {
        (activity as MainActivity).photoMap.put(place_url, photo_url)
    }

    override fun getUrl(place_url: String): String {
        return if ((activity as MainActivity).photoMap.containsKey(place_url))
            (activity as MainActivity).photoMap.getValue(place_url)
        else "null"
    }
}
