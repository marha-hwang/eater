package com.example.myapplication.ui.restaurant_info

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.RestaurantInfoFragmentBinding
import com.example.myapplication.ui.home.HomeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup

import java.net.MalformedURLException
import java.net.URL
import kotlin.RuntimeException
import kotlin.concurrent.thread

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

        //툴바 커스텀
        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)

        binding.button6.setOnClickListener {
            val nav = Navigation.findNavController(it)
            val action= Restaurant_InfoFragmentDirections.actionRestaurantInfoFragmentToReviewFragment(
                args.restaurantName,args.restaurantAddress
            )
            nav?.navigate(action)
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

            val doc = Jsoup.connect(xhr_url)
                .ignoreContentType(true).execute().body().toString()
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

            val comment = try {
                jsonObject.getJSONObject("comment").getJSONArray("list")
                    .getJSONObject(0).getString("contents")
            } catch (e: org.json.JSONException) {
                "정보없음"
            }

            activity?.runOnUiThread {
                binding.textView4.text = phonenum
                binding.textView5.text = menu
                binding.textView6.text = comment

                //식당이미지를 코루틴으로 불러오기
                CoroutineScope(Dispatchers.Main).launch {
                    if(!photoUrl.equals("정보없음")) {
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
                        binding.imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }

        return root
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