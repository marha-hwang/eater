package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.api.KaKaoApi
import com.example.myapplication.api.KaKaoApiService
import com.example.myapplication.api.RestaurantData
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_setting, R.id.navigation_mypage
            ), //최상위 레이아웃으로지정
        )

        setupActionBarWithNavController(navController, appBarConfiguration) //이동시 상단액션바(툴바)변경
        navView.setupWithNavController(navController) //프래그먼트 이동수행


    }
    override fun onSupportNavigateUp():Boolean{ //네비게이션 up버튼 동작
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp()
    }
    
}

