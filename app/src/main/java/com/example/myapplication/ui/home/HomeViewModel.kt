package com.example.myapplication.ui.home

import android.util.Log
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

data class Item(val icon: String, val firstName: String, val lastName: String)

class HomeViewModel : ViewModel() {
   /* val itemsListData = MutableLiveData<ArrayList<Item>>()
    val items = ArrayList<Item>()

    init {
        items.add(Item("person", "Yuh-jung", "Youn"))
        items.add(Item("person", "Steven", "Yeun"))
        items.add(Item("person", "Alan", "Kim"))
        items.add(Item("person outline", "Ye-ri", "Han"))
        items.add(Item("person outline", "Noel", "Cho"))
        items.add(Item("person pin", "Lee Issac", "Chung"))
        items.add(Item("person", "Yuh-jung", "Youn"))
        items.add(Item("person", "Steven", "Yeun"))
        items.add(Item("person", "Alan", "Kim"))
        items.add(Item("person outline", "Ye-ri", "Han"))
        items.add(Item("person outline", "Noel", "Cho"))
        items.add(Item("person pin", "Lee Issac", "Chung"))
        itemsListData.value = items
    }*/

    val RestaurantList = MutableLiveData<RestaurantData>()
    var RestaurantData:RestaurantData? = null

    init{
        searchKeyword("갈매동", "FD6")
        RestaurantList.value = RestaurantData
    }

    private fun searchKeyword(keyword: String, category: String){
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
            ){
                Log.d("ApiTest", "Raw: ${response.raw()}")
                Log.d("ApiTest", "Raw: ${response.body()}")
                RestaurantData = response.body()
            }
            override fun onFailure(call: Call<RestaurantData>, t: Throwable) {
                Log.e("Mainactivity", "통신실패: ${t.message}")
            }
        })
    }
}