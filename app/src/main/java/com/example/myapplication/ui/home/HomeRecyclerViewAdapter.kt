package com.example.myapplication.ui.home


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.api.Place
import com.example.myapplication.databinding.ItemListBinding
import com.example.myapplication.ui.restaurant_info.ImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.random.Random

class HomeRecyclerViewAdapter(private val viewModel: HomeViewModel, private val context: Context) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    var placeList = mutableListOf<Place>() //데이터 세팅을 위한 변수
    var photoMap = HashMap<String, String>() // 사진url을 저장하기 위한맵

    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(place_name: String, address_name: String, place_url: String) {
            binding.textView.text = place_name
            //binding.textView2.text = address_name
            binding.textView3.text = place_url

            //불러온 이미지 바인딩하기
            thread(start = true) {
                if(!photoMap.containsKey(place_url)){
                    var url = place_url.split("/")
                    var xhr_url = "https://place.map.kakao.com/main/v/" + url[3]
                    Log.d("xhr_url", xhr_url)
                    val doc = try {
                        Jsoup.connect(xhr_url)
                            .ignoreContentType(true).execute().body().toString()
                    } catch (e: org.jsoup.HttpStatusException) {
                        "정보없음"
                    }
                    Log.e("thread id ", Thread.currentThread().toString())
                    Log.d("doc ", doc)
                    if(!doc.equals("정보없음")) {
                        val jsonObject = JSONObject(doc)

                        val photoUrl = try {
                            jsonObject.getJSONObject("basicInfo").getString("mainphotourl")
                        } catch (e: org.json.JSONException) {
                            "정보없음"
                        }
                        photoMap.put(place_url, photoUrl)
                }

                    //식당이미지를 코루틴으로 불러오기
                    CoroutineScope(Dispatchers.Default).launch {
                        if (!photoMap.getValue(place_url).equals("정보없음")) {
                            var urlArg = photoMap.getValue(place_url).split("/")
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
                                binding.imageView.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }

            binding.CardView.setOnClickListener {
                val nav = findNavController(it)
                val action = HomeFragmentDirections.actionNavigationHomeToRestaurantInfoFragment4(
                    place_name, address_name, place_url
                )
                nav?.navigate(action)
            }
            /* binding.CardView.setOnClickListener{ //activity를 통한 화면전환시 사용
                    val nextIntent = Intent(context, Restaurant_INFO::class.java)
                    context.startActivity(nextIntent)*/


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            placeList[position].place_name,
            placeList[position].address_name,
            placeList[position].place_url,
        )
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return if (placeList == null) 0
        else placeList.size
    }

    fun setData(data: ArrayList<Place>) {
        placeList = data
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
}