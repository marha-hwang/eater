package com.example.myapplication.ui.dashboard// REST API 통신 인터페이스
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/local/search/keyword.json")    // Keyword.json의 정보를 받아옴
    fun getSearchKeyword(
        @Header("Authorization") key: String,     // 카카오 API 인증키 [필수]
        @Query("query") query: String,            // 검색을 원하는 질의어 [필수]
        @Query("page") page: Int,               // 결과 페이지 번호
        @Query("x") x : String,
        @Query("y") y : String,
        @Query("radius") radius : Int,

    ): Call<ResultSearchKeyword>    // 받아온 정보가 com.example.myapplication.ui.dashboard.ResultSearchKeyword 클래스의 구조로 담김
}