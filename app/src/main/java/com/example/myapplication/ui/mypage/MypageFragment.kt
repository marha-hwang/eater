package com.example.myapplication.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentMypageBinding
import com.kakao.sdk.user.UserApiClient

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mypageViewModel =
            ViewModelProvider(this).get(MypageViewModel::class.java)

        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var userID: String
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(error.toString(), "사용자 정보 요청 실패")
                Toast.makeText(requireContext(), "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                val nav = Navigation.findNavController(root)
                val action = MypageFragmentDirections.actionNavigationMypageToNavigationSetting()
                nav?.navigate(action)
            } else {
                userID = user?.id.toString()

                val likeAdapter = MylikesAdapter(requireContext())
                val reviewAdapter = MyreviewsAdapter(requireContext())
                val commentAdapter = MycommentAdapter(requireContext())
                binding.likeView.isVisible = false
                binding.reviewView.isVisible = false
                binding.commentView.isVisible = false

                mypageViewModel.LoadlikeLes(userID)
                mypageViewModel.LoadReview(userID)
                mypageViewModel.LoadComment(userID)

                binding.likeView.adapter = likeAdapter
                binding.likeView.layoutManager = GridLayoutManager(activity, 2)
                mypageViewModel.LikeList.observe(viewLifecycleOwner){
                    likeAdapter.notifyDataSetChanged()
                    likeAdapter.setData(it)
                }

                binding.reviewView.adapter = reviewAdapter
                binding.reviewView.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                mypageViewModel.ReviewList.observe(viewLifecycleOwner) {
                    reviewAdapter.notifyDataSetChanged()
                    reviewAdapter.setData(it)
                }

                binding.commentView.adapter = commentAdapter
                binding.commentView.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                mypageViewModel.CommentList.observe(viewLifecycleOwner) {
                    commentAdapter.notifyDataSetChanged()
                    commentAdapter.setData(it)
                }
            }
        }

        binding.mylikeBtn.setOnClickListener {
            if(binding.mylikeBtn.text.toString().equals("열기")) {
                binding.likeView.isVisible = true
                binding.mylikeBtn.text = "닫기"
            }
            else {
                binding.mylikeBtn.text = "열기"
                binding.likeView.isVisible = false
            }
        }

        binding.reviewBtn.setOnClickListener {
            if(binding.reviewBtn.text.toString().equals("열기")) {
                binding.reviewView.isVisible = true
                binding.reviewBtn.text = "닫기"
            }
            else {
                binding.reviewBtn.text = "열기"
                binding.reviewView.isVisible = false
            }
        }
        binding.CommentBtn.setOnClickListener {
            if(binding.CommentBtn.text.toString().equals("열기")) {
                binding.commentView.isVisible = true
                binding.CommentBtn.text = "닫기"
            }
            else {
                binding.CommentBtn.text = "열기"
                binding.commentView.isVisible = false
            }
        }
        return root
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}