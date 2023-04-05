package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel

import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {
      val REQUST_LOCATION=1
     var marker: Marker? = null
    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var map: GoogleMap
    private val TAG = SelectLocationFragment::class.java.simpleName
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)



        binding.saveButton.setOnClickListener {
            Log.i(TAG,"clicked")
            onLocationSelected()
        }
        return binding.root
    }

    private fun onLocationSelected() {
        Log.i(TAG,marker.toString())
        if (marker!=null) {
            _viewModel.reminderSelectedLocationStr.value = marker!!.title
            _viewModel.latitude.value = marker!!.position.latitude
            _viewModel.longitude.value = marker!!.position.longitude
            _viewModel.navigationCommand.value = NavigationCommand.Back
        }
    }

    private fun showalert() {
        if (
         notgrainted()
        ) {
           Snackbar.make(requireView(),R.string.permission_denied_explanation,Snackbar.LENGTH_INDEFINITE).setAction("ok")
           {
              val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                      ActivityCompat.requestPermissions(
                            requireActivity(),
                         permissionsArray,
                            REQUST_LOCATION

                       )

           }.show()

        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        map=p0!!
        setonlongclilk(map)
        setpoi(map)
        setMapStyle(map)
        enable(map)

    }
    fun setpoi(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            Log.i(TAG,marker.toString())
            marker?.showInfoWindow()
            map.moveCamera(CameraUpdateFactory.newLatLng(poi.latLng))
        }
    }

    fun grainted():Boolean=ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    fun notgrainted():Boolean=ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_DENIED
    fun enable(map: GoogleMap)
    {
        if (grainted())
        {
            map.setMyLocationEnabled(true)
        }else
        {
          showalert()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }
    private fun setMapStyle(map: GoogleMap) {
        try {

            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }
    fun setonlongclilk(map: GoogleMap) {

        map.setOnMapLongClickListener { latLong ->
            val snap = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f,Long %2$.5f",
                latLong.latitude,
                latLong.longitude
            )
          marker=map.addMarker(MarkerOptions().position(latLong).title("drop here").snippet(snap).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            showalert()
        } else {
            showalert()
        }
    }

}

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            bool
        )
    }
}

