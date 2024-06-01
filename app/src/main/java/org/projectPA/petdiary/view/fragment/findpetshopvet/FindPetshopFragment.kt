package org.projectPA.petdiary.view.fragment.findpetshopvet

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.projectPA.petdiary.databinding.FragmentFindPetshopBinding

class FindPetshopFragment : Fragment() {
    private lateinit var binding: FragmentFindPetshopBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFindPetshopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLocationAndLoadMap()
    }

    private fun getLocationAndLoadMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                loadMapWithCoordinates(latitude, longitude)
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun loadMapWithCoordinates(latitude: Double, longitude: Double) {
        val url = "https://www.google.com/maps/search/Petshop/@$latitude,$longitude,15z"
        binding.petshopWV.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocationAndLoadMap()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}