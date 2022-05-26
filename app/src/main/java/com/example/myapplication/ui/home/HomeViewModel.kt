package com.example.myapplication.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.sleep

class HomeViewModel : ViewModel() {

    val RestaurantList = MutableLiveData<ArrayList<Place>>()
    val places = ArrayList<Place>()

    val address = MutableLiveData<String>()
    val region_2depth_name = MutableLiveData<String>()
    val region_3depth_name = MutableLiveData<String>()

    init{

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
                Log.e("ApiTest", "Raw: ${response.raw()}")
                Log.e("ApiTest", "Raw: ${response.body()}")
                places.clear()
                if (response.body()!=null) {
                    for (i: Int in 0 until response.body()!!.documents.size) {
                        places.add(
                            Place(
                                response.body()?.documents!!.get(i)!!.place_name,
                                response.body()?.documents!!.get(i)!!.address_name,
                                response.body()?.documents!!.get(i)!!.road_address_name,
                                response.body()?.documents!!.get(i)!!.place_url,
                                response.body()?.documents!!.get(i)!!.category_name,
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

    fun searchcategory( x: String, y: String, category: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(KaKaoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KaKaoApiService::class.java)
        places.clear()
        for(i in 1..3) {
            val call = api.getSearchCategory(KaKaoApi.API_KEY, "FD6", i, x, y, 1000)
            call.enqueue(object : Callback<RestaurantData> {
                override fun onResponse(
                    call: Call<RestaurantData>,
                    response: Response<RestaurantData>
                ) {
                    Log.e("ApiTest", "Raw${i}: ${response.raw()}")
                    Log.e("ApiTest", "Raw${i}: ${response.body()}")
                    //Log.e("ApiTest", "count: ${response.body()!!.documents.size.toString()}")

                    if (response.body() != null) {
                        for (i: Int in 0 until response.body()!!.documents.size) {
                            if(response.body()?.documents!!.get(i)!!.category_name.contains(category)) {
                                places.add(
                                    Place(
                                        response.body()?.documents!!.get(i)!!.place_name,
                                        response.body()?.documents!!.get(i)!!.address_name,
                                        response.body()?.documents!!.get(i)!!.road_address_name,
                                        response.body()?.documents!!.get(i)!!.place_url,
                                        response.body()?.documents!!.get(i)!!.category_name,
                                        response.body()?.documents!!.get(i)!!.x,
                                        response.body()?.documents!!.get(i)!!.y
                                    )
                                )
                            }
                        }
                    } else {
                        Log.d("ApiTest", "Raw: 검색결과가 없습니다")
                    }
                    RestaurantList.value = places
                }

                override fun onFailure(call: Call<RestaurantData>, t: Throwable) {
                    Log.e("Mainactivity", "통신실패: ${t.message}")
                }
            })
            sleep(100)
        }
    }

    fun searchAddress(x: String, y: String, coord: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(KaKaoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KaKaoApiService::class.java)
        val call = api.getAddressFromCoordinate(KaKaoApi.API_KEY, x, y, coord)

        call.enqueue(object : Callback<AddressData> {
            override fun onResponse(
                call: Call<AddressData>,
                response: Response<AddressData>
            ) {
                Log.d("ApiTest", "Raw: ${response.raw()}")
                Log.d("ApiTest", "Raw: ${response.body()}")

                if (response.body()!!.documents.size != 0) {
                    address.value = response.body()!!.documents.get(0).address.address_name
                    region_2depth_name.value = response.body()!!.documents.get(0).address.region_2depth_name
                    region_3depth_name.value = response.body()!!.documents.get(0).address.region_3depth_name

                } else {
                    Log.d("ApiTest", "Raw: 검색결과가 없습니다")
                }
            }

            override fun onFailure(call: Call<AddressData>, t: Throwable) {
                Log.e("Mainactivity", "통신실패: ${t.message}")
            }
        })
    }
}