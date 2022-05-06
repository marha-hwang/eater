package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.ReviewsData
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.math.log

class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                loginErrorCode(error)
                Log.e(TAG, "로그인 실패",error)
            }

            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(error.toString(), "사용자 정보 요청 실패")
                    }
                    else {
                        /* 로그인 성공시 작업 부분 */

                        // 카카오 토큰
                        val userToken = token.accessToken
                        Log.e(TAG,"로그인 성공 ${userToken}")
                        // 카카오 id
                        val userId = user?.id.toString()
                        Log.e(TAG,"로그인 ID ${userId}")
                        // 카카오 이메일
                        val userEmail = user?.kakaoAccount?.email.toString()
                        Log.e(TAG,"로그인 이메일 ${userEmail}")
                        // 카카오 닉네임
                        val userNickname = user?.kakaoAccount?.profile?.nickname.toString()
                        Log.e(TAG,"로그인 닉네임 ${userNickname}")

                        val db: FirebaseFirestore = Firebase.firestore
                        val itemsCollectionRef = db.collection("Users")

                        val itemMap = hashMapOf(
                            "UserId" to userId,
                        )

                        itemsCollectionRef.whereEqualTo("UserId", userId).get().addOnSuccessListener {
                            if(it.isEmpty) {
                                itemsCollectionRef.add(itemMap) //새로운 document생성후 필드 추가
                                    .addOnSuccessListener {
                                        Log.e(TAG, "아이디 없음")
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Log.e(TAG, "사용자 아이디 등록 실패")
                                    }
                            } else{
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }.addOnFailureListener {
                            Log.e(TAG, "FireBase 접근 에러")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }

                }
            }
        }

        if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
            // 있는 경우 앱을 사용하여 로그인
            LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            // 없을 경우 웹 페이지를 띄움
            LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    // 에러 코드별 toast
    private fun loginErrorCode(error: Throwable) {
        when {
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.ServerError.toString() -> {
                Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
            }
            else -> { // Unknown
                Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
