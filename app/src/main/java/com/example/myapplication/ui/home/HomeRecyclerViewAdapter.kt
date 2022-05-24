package com.example.myapplication.ui.home


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.api.Place
import com.example.myapplication.databinding.ItemListBinding
import com.example.myapplication.ui.restaurant_info.ImageLoader
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.random.Random

class HomeRecyclerViewAdapter(var setURL_interface: setURL_interface, private val viewModel: HomeViewModel, private val context: Context) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    var placeList = mutableListOf<Place>() //데이터 세팅을 위한 변수

    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(place_name: String, address_name: String, place_url: String) {
            binding.textView.text = place_name
            //binding.textView2.text = address_name
            binding.textView3.text = place_url

            binding.CardView.setOnClickListener{
                val nav = findNavController(it)
                val action = HomeFragmentDirections.actionNavigationHomeToRestaurantInfoFragment4(
                    place_name, address_name, place_url
                )
                nav?.navigate(action)
            }

            //불러온 이미지 바인딩하기
            thread(start = true) {
                if (setURL_interface.getUrl(place_url).equals("null")) {
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
                    if (!doc.equals("정보없음")) {
                        val jsonObject = JSONObject(doc)

                        val photoUrl = try {
                            jsonObject.getJSONObject("basicInfo").getString("mainphotourl")
                        } catch (e: org.json.JSONException) {
                            "정보없음"
                        }
                        setURL_interface.setUrl(place_url, photoUrl)
                        Log.d("이미지", "이미지url가져옴")
                    }
                }


                //식당이미지를 코루틴으로 불러오기
                CoroutineScope(Dispatchers.Default).launch {
                    if (!setURL_interface.getUrl(place_url).equals("정보없음")) {
                        var urlArg = setURL_interface.getUrl(place_url).split("/")
                        var url = "https://"
                        for (i: Int in 2..urlArg.size - 1) {
                            if (i != urlArg.size - 1) {
                                url = url + urlArg[i] + "/"
                            } else url = url + urlArg[i]
                        }
                        Log.d("photo Url", url)

                        val storageReference = Firebase.storage.reference.child(url).child("res.jpg")
                        storageReference.downloadUrl.addOnSuccessListener {
                            CoroutineScope(Dispatchers.Main).launch {
                                Glide.with(context).load(it).into(binding.imageView)
                                Log.d("이미지", "이미지서버에서")
                            }
                        }.addOnFailureListener {
                            CoroutineScope(Dispatchers.Default).launch {
                                val bitmap = withContext(Dispatchers.IO) {
                                    ImageLoader.loadImage(url)
                                }
                                Log.d("이미지", "이미지카카오에서")
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.imageView.setImageBitmap(bitmap)
                                }
                                val baos = ByteArrayOutputStream()
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                val data = baos.toByteArray()
                                //파이어베이스에 올리기
                               try{
                                    var uploadTask = storageReference.putBytes(data)
                                    uploadTask.addOnFailureListener {
                                        Log.d("이미지", "이미지첨부실패")
                                    }
                                        .addOnSuccessListener {
                                            Log.d("이미지", "이미지첨부성공")
                                        }
                               }catch(e:java.lang.ClassCastException){ "이미지 첨부안함"}
                            }
                        }
                    }
                }
            }
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
