package com.example.myapplication.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.api.KaKaoApi
import com.example.myapplication.api.KaKaoApiService
import com.example.myapplication.api.Place
import com.example.myapplication.api.RestaurantData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel : ViewModel() {

    val RestaurantList = MutableLiveData<ArrayList<Place>>()
    val places = ArrayList<Place>()

    init{
        //searchKeyword("갈매동", "FD6")
        //Log.d("init abcd", RestaurantList.toString())
    }


    fun searchKeyword(keyword: String, category: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(KaKaoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KaKaoApiService::class.java)
        val call = api.getKaKaoAddress(KaKaoApi.API_KEY, keyword,category)

        call.enqueue(object: Callback<RestaurantData> {
            override fun onResponse(
                call: Call<RestaurantData>,
                response: Response<RestaurantData>
            ) {
                Log.d("ApiTest", "Raw: ${response.raw()}")
                Log.d("ApiTest", "Raw: ${response.body()}")
                places.clear()
                if (response.body()!=null) {
                    for (i: Int in 0 until response.body()!!.documents.size) {
                        places.add(
                            Place(
                                response.body()?.documents!!.get(i)!!.place_name,
                                response.body()?.documents!!.get(i)!!.address_name,
                                response.body()?.documents!!.get(i)!!.road_address_name,
                                response.body()?.documents!!.get(i)!!.place_url,
                                response.body()?.documents!!.get(i)!!.x,
                                response.body()?.documents!!.get(i)!!.y
                            )
                        )
                    }
                }
                else {
                    Log.d("ApiTest", "Raw: 검색결과가 없습니다")
                }
                RestaurantList.value = places
            }

            override fun onFailure(call: Call<RestaurantData>, t: Throwable) {
                Log.e("Mainactivity", "통신실패: ${t.message}")
            }
        })
    }
}