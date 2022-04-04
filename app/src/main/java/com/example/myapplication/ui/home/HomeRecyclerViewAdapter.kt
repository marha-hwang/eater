package com.example.myapplication.ui.home


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.api.Place
import com.example.myapplication.databinding.ItemListBinding

class HomeRecyclerViewAdapter(private val viewModel: HomeViewModel, private val context: Context) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    var placeList = mutableListOf<Place>() //데이터 세팅을 위한 변수

    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(place_name: String, address_name: String, x: String){
                binding.textView.text = place_name
                binding.textView2.text = address_name
                binding.textView3.text = x
                binding.CardView.setOnClickListener {
                    val nav = findNavController(it)
                    nav?.navigate(R.id.restaurant_InfoFragment)
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
        holder.bind(placeList[position].place_name,
            placeList[position].address_name,
           placeList[position].x,)
    }

    override fun getItemCount(): Int {
        return if(placeList==null) 0
        else placeList.size
    }

    fun setData(data : ArrayList<Place>){
        placeList = data
    }
}