package com.example.myapplication.ui.restaurant_info

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.RestaurantInfoFragmentBinding
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.google.android.gms.location.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup

import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.RuntimeException
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class Restaurant_InfoFragment : Fragment() {


    private var _binding: RestaurantInfoFragmentBinding? = null
    private lateinit var viewModel: RestaurantInfoViewModel
    private val binding get() = _binding!!

    var res_x = ""
    var res_y = ""
    //현재위치를 가져오기 위한 변수
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    //위치 값을 가지고 있는 객체
    lateinit var mLastLocation: Location

    //위치정보 요청의 매개변수를 저장하는 객체
    internal lateinit var mLocationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = RestaurantInfoFragmentBinding.inflate(inflater, container, false)
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
                Log.d("", "식당좌표" +res_x + ", " + res_y)
                if(mLastLocation.longitude.toString().contains(res_y) && mLastLocation.latitude.toString().contains(res_x)) {
                    Toast.makeText(requireContext(), "방문완료~~~~~!!!!!!", Toast.LENGTH_SHORT).show()
                }

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

        (activity as MainActivity).bottomNavigationShow(false)//bottom네비게이션 없애기

        val args by navArgs<Restaurant_InfoFragmentArgs>()
        binding.restaurantAddress.text = args.restaurantAddress
        binding.restaurantName.text = args.restaurantName

        //툴바 커스텀
        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)
        binding.toolbar.title = "식당정보 하트하또"

        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Restaurants")

        binding.visitBtn.setOnClickListener {
            startLocationUpdates()
        }

        binding.likeBtn.setOnClickListener {
            val itemMap = hashMapOf(
                "res_name" to args.restaurantName,
                "res_address" to args.restaurantAddress,
                "res_url" to args.restaurantUrl,
                "likes" to 0,
                "LikeUsers" to arrayListOf<String>()
            )
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(error.toString(), "사용자 정보 요청 실패")
                    Toast.makeText(requireContext(), "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    itemsCollectionRef.whereEqualTo("res_name", args.restaurantName).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                                    .addOnSuccessListener {
                                        //식당문서 변경
                                        it.update("likes", FieldValue.increment(1))
                                        it.update("LikeUsers", FieldValue.arrayUnion(user?.id.toString()))
                                        Toast.makeText(requireContext(), "좋아요를 눌렀습니다!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {

                                    }
                            } else {
                                itemsCollectionRef.whereEqualTo("res_address", args.restaurantAddress).get()
                                    .addOnSuccessListener {
                                        if (it.isEmpty) {
                                            itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                                                .addOnSuccessListener {
                                                    //식당문서 변경
                                                    it.update("likes", FieldValue.increment(1))
                                                    it.update("LikeUsers", FieldValue.arrayUnion(user?.id.toString()))
                                                    Toast.makeText(requireContext(), "좋아요를 눌렀습니다!", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener {
                                                }
                                        } else {
                                            //존재하는 문서변경 좋아요 수 추가랑 유저 리스트 추가
                                            var userList: ArrayList<String> = it.documents.get(0).get("LikeUsers") as ArrayList<String>
                                            if (!userList.contains(user?.id.toString())) {
                                            Log.d("", it.documents.get(0).id)
                                            itemsCollectionRef.document(it.documents.get(0).id)
                                                .update("likes", FieldValue.increment(1))
                                            itemsCollectionRef.document(it.documents.get(0).id)
                                                .update("LikeUsers", FieldValue.arrayUnion(user?.id.toString())
                                                )
                                                Toast.makeText(requireContext(), "좋아요를 눌렀습니다!", Toast.LENGTH_SHORT).show()
                                            }else {
                                                Toast.makeText(requireContext(), "이미 좋아요를 눌렀습니다! ", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .addOnFailureListener {}
                            }
                        }
                        .addOnFailureListener {
                        }
                }
            }

        }

        binding.reviewBtn.setOnClickListener {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(error.toString(), "사용자 정보 요청 실패")
                    Toast.makeText(requireContext(), "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    val nav = Navigation.findNavController(it)
                    val action= Restaurant_InfoFragmentDirections.actionRestaurantInfoFragmentToReviewFragment(
                        args.restaurantName,args.restaurantAddress
                    )
                    nav?.navigate(action)
                }
            }
        }

        binding.MoreReviews.setOnClickListener {
            val nav = Navigation.findNavController(it)
            val action= Restaurant_InfoFragmentDirections.actionRestaurantInfoFragmentToReviewsOfRestaurantFragment(
                args.restaurantName,args.restaurantAddress
            )
            nav?.navigate(action)
        }

        thread(start = true) {
            var url = args.restaurantUrl.split("/")
            var xhr_url = "https://place.map.kakao.com/main/v/" + url[3]
            Log.d("xhr_url", xhr_url)

            val doc = try {
                Jsoup.connect(xhr_url)
                    .ignoreContentType(true).execute().body().toString()
            } catch (e: org.jsoup.HttpStatusException) {
                "정보없음"
            }
            if (!doc.equals("정보없음")) {
                val jsonObject = JSONObject(doc) //문자열을 json객체로변환

                val photoUrl = try {
                    jsonObject.getJSONObject("basicInfo").getString("mainphotourl")
                } catch (e: org.json.JSONException) {
                    "정보없음"
                }

                val phonenum = try {
                    jsonObject.getJSONObject("basicInfo").getString("phonenum")
                } catch (e: org.json.JSONException) {
                    "정보없음"
                }

                val menu = try {
                    jsonObject.getJSONObject("menuInfo").getJSONArray("menuList")
                        .getJSONObject(0).getString("menu")
                } catch (e: org.json.JSONException) {
                    "정보없음"
                }
                res_x = try{
                    jsonObject.getJSONObject("basicInfo").getString("wpointx")
                } catch (e: org.json.JSONException)   {
                    "정보없음"
                }
                res_y = try{
                    jsonObject.getJSONObject("basicInfo").getString("wpointy")
                } catch (e: org.json.JSONException)   {
                    "정보없음"
                }

                val comment = try {
                    jsonObject.getJSONObject("comment").getJSONArray("list")
                        .getJSONObject(0).getString("contents")
                } catch (e: org.json.JSONException) {
                    "정보없음"
                }

                activity?.runOnUiThread {
                    binding.phoneNum.text = phonenum
                    binding.menuBtn.text = menu
                    binding.reviewComment.text = comment

                    //식당이미지를 코루틴으로 불러오기
                    CoroutineScope(Dispatchers.Default).launch {
                        if (!photoUrl.equals("정보없음")) {
                            var urlArg = photoUrl.split("/")
                            var url = "https://"
                            for (i: Int in 2..urlArg.size - 1) {
                                if (i != urlArg.size - 1) {
                                    url = url + urlArg[i] + "/"
                                } else url = url + urlArg[i]
                            }
                            Log.d("photo Url", url)


                            val bitmap = withContext(Dispatchers.IO) {
                                ImageLoader.loadImage(url)
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.resImg.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).bottomNavigationShow(true)
        Log.d("","bottom info")
    }

}

//식당 이미지를 불러오는 함수
object ImageLoader {
    suspend fun loadImage(imageurl: String): Bitmap? {
        val bmp: Bitmap? = null
        try {
            val url = URL(imageurl)
            val stream = url.openStream()
            return BitmapFactory.decodeStream(stream)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return bmp
    }
}