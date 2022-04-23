package com.example.myapplication.api


import com.google.gson.annotations.SerializedName

data class RestaurantData(
    var documents: ArrayList<Place>
)

data class Place(
    var place_name: String,             // 장소명, 업체명
    var address_name: String,           // 전체 지번 주소
    var road_address_name: String,      // 전체 도로명 주소
    var place_url: String,               // 식당페이지 url
    var x: String,                      // X 좌표값 혹은 longitude
    var y: String                      // Y 좌표값 혹은 latitude
)

data class AddressData(
    var documents: ArrayList<TotalAddress>
)

data class TotalAddress(
    var address: Address,
    var road_address: RoadAddress
)

data class Address(
    var address_name: String,
    var region_1depth_name: String, // 시,도단위
    var region_2depth_name: String, // 구단위
    var region_3depth_name: String,  // 동단위
    var main_address_no: String, // 지번 주번지
    var sub_address_no: String // 지번 부번지
)

data class RoadAddress(
    var address_name: String,
    var region_1depth_name: String, // 시,도단위
    var region_2depth_name: String, // 구단위
    var region_3depth_name: String,  // 동단위
    var road_name: String, // 지번 주번지
    var building_name: String, // 지번 부번지
    var zone_no: String
)

