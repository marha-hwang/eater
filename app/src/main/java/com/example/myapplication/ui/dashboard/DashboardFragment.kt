package com.example.myapplication.ui.dashboard

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.LoginActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.example.myapplication.ui.mypage.MypageFragmentDirections
import com.example.myapplication.ui.notifications.NotificationsFragmentDirections
import com.example.myapplication.ui.restaurant_info.Restaurant_InfoFragment
import com.google.android.gms.location.*
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.HashMap


class DashboardFragment : Fragment(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK ad9588c849020194182ad63bfb3285b3"  // REST API 키
    }

    // GPS 마커 변수
    //현재위치를 가져오기 위한 변수
    private  var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    //위치 값을 가지고 있는 객체
    lateinit var mLastLocation: Location
    //위치정보 요청의 매개변수를 저장하는 객체
    internal lateinit var mLocationRequest: LocationRequest

    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    private var x : String = "126.978652258823"
    private var y : String = "37.56682420267543"
    private val markerInfo:HashMap<String, String> = HashMap()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 장소 검색
        binding.rvList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter




        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object : com.example.myapplication.ui.dashboard.ListAdapter.OnItemClickListener  {
            override fun onClick(v: View, position: Int) {
                val mapPoint =
                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })

        // 검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }
        binding.btnZoomButton.setOnClickListener{
            binding.mapView.zoomIn(true)
        }
        binding.btnZoomOutButton.setOnClickListener{
            binding.mapView.zoomOut(true)
        }

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }



        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        //mFusedLocationProviderClient 변수 초기화
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //mLocationRequest 변수 초기화
        mLocationRequest = LocationRequest.create().apply{
            //interval = 2000 // 업데이트 간격 단위(밀리초)
            fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            maxWaitTime= 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        //시스템으로 부터 위치 정보를 콜백으로 받은 후 UI작업과 api작업을 수행하는 함수
        val mLocationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                try { // 다른화면 전환시 NullPointerException이 계속 발생하여 try catch 문을 넣어줌
                    //binding.mapView.removeAllPOIItems()
                    binding.mapView.removeAllCircles()//써클제거
                    mLastLocation = locationResult.lastLocation
                    x = mLastLocation.longitude.toString()
                    y = mLastLocation.latitude.toString()
                    Log.d(
                        ContentValues.TAG,"")
                    val point = MapPOIItem()
                    point.apply {
                        itemName = "현재 위치"
                        mapPoint = MapPoint.mapPointWithGeoCoord(mLastLocation.latitude,
                            mLastLocation.longitude)
                        markerType = MapPOIItem.MarkerType.YellowPin
                        selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    }
                    val circle1 = MapCircle(
                        MapPoint.mapPointWithGeoCoord(y.toDouble(), x.toDouble()),  // center
                        1000,  // radius
                        Color.argb(30,55,145,181),  // strokeColor
                        Color.argb(30,55, 145, 181) // fillColor
                    )

                    binding.mapView.addPOIItem(point)
                    binding.mapView.addCircle(circle1)
                    binding.mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mLastLocation.latitude, mLastLocation.longitude), 5,true)

                    searchKeyword("음식점",1)




                }catch(e: java.lang.NullPointerException){
                    Log.d("remove catch", "try catch Remove Marker")
                }
            }
        }




        //위치 업데이트를 하는 함수
        fun startLocationUpdates() {

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Log.d(ContentValues.TAG, "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            }
            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper()!!)
        }

        //위치정보를 가져오는 함수를 호출
        binding.btnStart.setOnClickListener {
            if ((activity as MainActivity).checkPermissionForLocation(requireContext())) { //권한 요청
                startLocationUpdates()
            }
        }




        fun zoomOut() {//줌아웃
            binding.mapView.zoomOut(true)
        }

        fun zoomIn() {//줌인
            binding.mapView.zoomIn(true)
        }

        //지도 맵을 확대하기
        binding.btnZoomButton.setOnClickListener{
            zoomIn()
        }
        //지도 맵 축소하기
        binding.btnZoomOutButton.setOnClickListener{
            zoomOut()
        }
        //지도클릭시 키보드 사라지기
        binding.mapView.setMapViewEventListener(this)
        //마커 말풍선 클릭시 식당상세로 이동
        binding.mapView.setPOIItemEventListener(this)


        return root
    }


    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        val radius:Int = 9999 // 3000에서 1000으로 변경.

        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY,keyword,"FD6", page, x, y, radius)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거

            //여기에 현재 위치 추가하는걸로
            for (document in searchResult!!.documents) {
                val item = ListLayout(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
                //여기에 내 위치 추가
                markerInfo.put(point.itemName, document.address_name +"," + document.place_url)

                val mypoint = MapPOIItem()
                mypoint.apply {
                    itemName = "현재 위치"
                    mapPoint = MapPoint.mapPointWithGeoCoord(mLastLocation.latitude,
                        mLastLocation.longitude)
                    markerType = MapPOIItem.MarkerType.YellowPin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(mypoint)



            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화




        } else {
            // 검색 결과 없음
            Toast.makeText(activity, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
        //
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //지도 이벤트 처리 함수 override구현
    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        (activity as MainActivity).dismissKeyboard()
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
        if(!p1!!.itemName.equals("현재 위치")) {
            val name = p1!!.itemName
            val address = markerInfo.get(name).toString().split(",")[0]
            val url = markerInfo.get(name).toString().split(",")[1]
            Log.d("", name + "     " + address + "     " + url)
            val nav = p0?.let { Navigation.findNavController(it) }
            val action =
                DashboardFragmentDirections.actionNavigationDashboardToRestaurantInfoFragment(
                    p1!!.itemName, address, url
                )
            nav?.navigate(action)

        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
}