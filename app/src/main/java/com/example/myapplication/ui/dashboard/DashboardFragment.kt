package com.example.myapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //kakao maps Api 구간
       // val mapView = MapView(activity)
        //val mapViewContainer = binding.mapView
        //mapViewContainer.addView(mapView)


        val mapView = MapView(activity)
        val mapViewContainer: ViewGroup = root.findViewById(R.id.map_view)
        mapViewContainer.addView(mapView)

        //
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}