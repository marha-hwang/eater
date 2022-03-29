package com.example.myapplication.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.Restaurant_INFO
import com.example.myapplication.databinding.ItemListBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.security.AccessController.getContext

class HomeRecyclerViewAdapter(private val viewModel: HomeViewModel, private val context: Context) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(icon: String, firstName: String, lastName: String){
                binding.textView.text = icon
                binding.textView2.text = firstName
                binding.textView3.text = lastName
                binding.CardView.setOnClickListener {

                }
               /* binding.CardView.setOnClickListener{ //activity를 통한 화면전환시 사용
                    val nextIntent = Intent(context, Restaurant_INFO::class.java)
                    context.startActivity(nextIntent)*/


            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel.items[position].icon,viewModel.items[position].firstName,viewModel.items[position].lastName)
    }

    override fun getItemCount() = viewModel.items.size
}