package com.example.myapplication.ui.mypage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.api.LikesData
import com.example.myapplication.databinding.ItemListBinding
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.jsoup.Jsoup
import kotlin.concurrent.thread

class MylikesAdapter(val context: Context) :RecyclerView.Adapter<MylikesAdapter.ViewHolder>() {

    var likeList = mutableListOf<LikesData>()

    inner class ViewHolder(private  val binding: ItemListBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurantName: String, restaurantAddress: String, restaurantUrl: String) {
            binding.textView.text = restaurantName
            binding.textView3.text = restaurantAddress

            binding.CardView.setOnClickListener {
                val nav = Navigation.findNavController(it)
                val action =
                    MypageFragmentDirections.actionNavigationMypageToRestaurantInfoFragment(
                        restaurantName, restaurantAddress, restaurantUrl
                    )
                nav?.navigate(action)
            }

            thread(start = true) {
                var url = restaurantUrl.split("/")
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

                            val storageReference =
                                Firebase.storage.reference.child(url).child("res.jpg")
                            storageReference.downloadUrl.addOnSuccessListener {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Glide.with(context).load(it).into(binding.resImg)
                                    Log.d("이미지", "이미지서버에서")
                                }
                            }
                        }

                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MylikesAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(
           likeList[position].restaurant_name,
           likeList[position].restaurant_address,
           likeList[position].restaurant_url
       )
    }

    override fun getItemCount(): Int {
        return if(likeList == null) 0
        else likeList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setData(data : ArrayList<LikesData>){
        likeList = data
    }

}