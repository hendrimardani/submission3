package com.example.mysubmission3.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.mysubmission3.R
import com.example.mysubmission3.data.api.response.ListStoriesWithLocation

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mysubmission3.databinding.ActivityMapsBinding
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        addManyMarkersStories()
    }

    private fun addManyMarkersStories() {
        viewModel.getAllStoriesWithLocation()
        viewModel.getListStoriesWithLocation().observe(this) { listStoriesWithLocationObserve ->
            Log.d("$TAG getListStoriesWithLocation()", listStoriesWithLocationObserve.toString())
            listStoriesWithLocationObserve.forEach { item ->
                val latLng = LatLng(item.lat!!, item.lon!!)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(item.name)
                        .snippet(item.description)
                )
                boundsBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.if (isLoading) View.VISIBLE else View.INVISIBLE
//    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
    }
}