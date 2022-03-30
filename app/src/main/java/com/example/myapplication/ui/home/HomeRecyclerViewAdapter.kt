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
            fun bind(place_name: String, address_name: String, x: String){
                binding.textView.text = place_name
                binding.textView2.text = address_name
                binding.textView3.text = x
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
        holder.bind(viewModel.RestaurantData!!.documents!!.get(position)!!.place_name,
            viewModel.RestaurantData!!.documents!!.get(position)!!.address_name,
            viewModel.RestaurantData!!.documents!!.get(position)!!.x)
    }

    override fun getItemCount(): Int {
        return if(viewModel.RestaurantData==null) 0
        else viewModel.RestaurantData!!.documents!!.size
    }
}