package org.abubaker.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.databinding.ActivityAddHappyPlaceBinding
import org.abubaker.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import org.abubaker.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {

    // Binding Object
    private lateinit var binding: ActivityHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate Layout (XML)
        binding = DataBindingUtil.setContentView(
            this@HappyPlaceDetailActivity,
            R.layout.activity_happy_place_detail
        )

        // After launching the activity we will check and get the Serializable data class with the details in it and set it to UI components.)
        var happyPlaceDetailModel: HappyPlaceModel? = null

        // We are checking if the INTENT has EXTRA information
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {

            // get the Serializable data model class with the details in it
            happyPlaceDetailModel =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel

        }

        //
        if (happyPlaceDetailModel != null) {

            // Enabling Support for the Toolbar
            setSupportActionBar(binding.toolbarHappyPlaceDetail)

            // Activating the Toolbar
            val actionbar = supportActionBar

            //
            if (actionbar != null) {
                actionbar!!.setDisplayHomeAsUpEnabled(true)
                actionbar!!.title = happyPlaceDetailModel.title

            }

            // Navigate the main activity on clicking the back button inside the action bar.
            binding.toolbarHappyPlaceDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            binding.tvDescription.text = happyPlaceDetailModel.description
            binding.tvLocation.text = happyPlaceDetailModel.location
        }

    }

}