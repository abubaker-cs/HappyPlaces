package org.abubaker.happyplaces.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.databinding.ActivityMapBinding
import org.abubaker.happyplaces.models.HappyPlaceModel

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    // Binding Object
    private lateinit var binding: ActivityMapBinding

    // Create a variable for data model class.
    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate Layout (XML)
        binding = DataBindingUtil.setContentView(
            this@MapActivity,
            R.layout.activity_map
        )


        // Receives the details through intent and used further.
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {

            //
            mHappyPlaceDetails = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACE_DETAILS
            ) as HappyPlaceModel?

        }

        // If we received the data
        if (mHappyPlaceDetails != null) {

            // Enabling Support for the Toolbar
            setSupportActionBar(binding.toolbarMap)

            // Activating the Toolbar
            val actionbar = supportActionBar

            //
            if (actionbar != null) {
                actionbar.setDisplayHomeAsUpEnabled(true)
                actionbar.title = mHappyPlaceDetails!!.title
            }

            // Navigate the main activity on clicking the back button inside the action bar.
            binding.toolbarMap.setNavigationOnClickListener {
                onBackPressed()
            }

            // We are trying to retrieve #map from the activity_map.xml file
            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

            // Sync Map
            supportMapFragment.getMapAsync(this)
        }


    }

    // After extending an interface adding the location pin to the map when the map is ready using the latitude and longitude.)
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */
        // Our GeoLocation retrieve from the data received
        val position = LatLng(
            mHappyPlaceDetails!!.latitude,
            mHappyPlaceDetails!!.longitude
        )

        // Our Marker
        googleMap.addMarker(
            MarkerOptions().position(position).title(mHappyPlaceDetails!!.location)
        )

        // Important: Animate while Auto Zooming to our GeoLocation
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)

    }

}