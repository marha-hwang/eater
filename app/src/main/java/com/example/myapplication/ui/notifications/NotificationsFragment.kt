package com.example.myapplication.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private var bestReviewID = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = NotificationsAdapter(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        LoadBestReview()
        notificationsViewModel.LoadReviews()

        notificationsViewModel.ReviewList.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
            adapter.setData(it)
        }
    /*    val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef: StorageReference? = storageRef.child("images")
        imagesRef*/


        binding.constraintLayout2.setOnClickListener {
            val nav = Navigation.findNavController(it)
            val action =
                NotificationsFragmentDirections.actionNavigationNotificationsToDetailReviewFragment(
                    bestReviewID
                )
            nav?.navigate(action)
            Log.d("id", "id= " + bestReviewID)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun LoadBestReview(){
        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("Reviews").orderBy("likes", Query.Direction.DESCENDING)

        itemsCollectionRef.get().addOnSuccessListener {
            bestReviewID = it.documents.get(0).id
            binding.bestTitle.text =  it.documents.get(0).get("restaurant_name").toString()
            binding.bestContent.text =  it.documents.get(0).get("title").toString()
            binding.bestLikes.text =  it.documents.get(0).get("likes").toString()
        }
    }
}