package com.example.myapplication


import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.home.HomeFragment
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer


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

        //setupActionBarWithNavController(navController, appBarConfiguration) //이동시 상단액션바(툴바)변경
        navView.setupWithNavController(navController) //프래그먼트 이동수행

    }

    override fun onSupportNavigateUp():Boolean{ //네비게이션 up버튼 동작
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp()
    }

    //터치 발생시 이벤트 처리
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dismissKeyboard()
        return true
    }

    // 키보드를 없애는 함수
    fun dismissKeyboard(){
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


    //사용자 권한에 대해 확인을 하는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 10) {
           if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

           }
       }
    }
    //사용자에게 위치 권한 요청을 보내는 함수
    fun checkPermissionForLocation(context: Context): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : O")
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : X")
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 10)
                false
            }
        } else {
            true
        }
    }
}

// Dongjun test4 2022-04-11
