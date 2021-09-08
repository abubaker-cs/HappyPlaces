package org.abubaker.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import org.abubaker.happyplaces.databinding.ActivityMapBinding
import org.abubaker.happyplaces.models.HappyPlaceModel

class MapActivity : AppCompatActivity() {

    // Binding Object
    private lateinit var binding: ActivityMapBinding

    // Create a variable for data model class.
    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Inflate Layout (XML)
        binding = DataBindingUtil.setContentView(
            this@MapActivity,
            R.layout.activity_map
        )

        // Enabling Support for the Toolbar
        setSupportActionBar(binding.toolbarMap)

        // Activating the Toolbar
        val actionbar = supportActionBar

        //
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        // Navigate the main activity on clicking the back button inside the action bar.
        binding.toolbarMap.setNavigationOnClickListener {
            onBackPressed()
        }

        // Receives the details through intent and used further.
        // START
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceDetails =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }


    }

}