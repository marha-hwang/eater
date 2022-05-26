package com.example.myapplication.ui.setting

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.LoginActivity
//import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.FragmentSettingBinding
import com.kakao.sdk.user.UserApiClient


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        settingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        binding.Loginbtn.setOnClickListener{
            val intent = Intent(this@SettingFragment.requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        binding.Logoutbtn.setOnClickListener{
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                } else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
        }
        /*binding.Unlinkbtn.setOnClickListener{
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "연결 끊기 실패", error)
                }
                else {
                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                }
            }
        }*/

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}