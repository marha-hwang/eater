package com.example.myapplication.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class KaKaoApi {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 8a6afe180137dd029d61903af111112c"
    }
}

interface KaKaoApiService{
    @GET("v2/local/search/keyword.json")
    fun getKaKaoAddress(
        @Header("Authorization") key:String,
        @Query("query") address:String,
        @Query("category_group_code") category:String
    ): Call<RestaurantData>

    @GET("v2/local/geo/coord2address.json")
    fun getAddressFromCoordinate(
        @Header("Authorization") key:String,
        @Query("x") x:String,
        @Query("y") y:String,
        @Query("input_coord") coord:String
    ): Call<AddressData>
}
