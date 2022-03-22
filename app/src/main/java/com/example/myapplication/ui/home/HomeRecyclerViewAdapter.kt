package com.example.myapplication.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemListBinding

class HomeRecyclerViewAdapter(private val viewModel: HomeViewModel) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(icon: String, firstName: String, lastName: String){
                binding.textView.text = icon
                binding.textView2.text = firstName
                binding.textView3.text = lastName

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