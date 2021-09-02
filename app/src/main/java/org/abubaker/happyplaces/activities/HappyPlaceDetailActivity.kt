package org.abubaker.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.databinding.ActivityAddHappyPlaceBinding
import org.abubaker.happyplaces.databinding.ActivityHappyPlaceDetailBinding

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

        // Enabling Support for the Toolbar
        setSupportActionBar(binding.toolbarHappyPlaceDetail)

        // Activating the Toolbar
        val actionbar = supportActionBar

        //
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        // Navigate the main activity on clicking the back button inside the action bar.
        binding.toolbarHappyPlaceDetail.setNavigationOnClickListener {
            onBackPressed()
        }

        // setContentView(R.layout.activity_happy_place_detail)
    }

}