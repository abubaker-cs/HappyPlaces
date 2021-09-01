package org.abubaker.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.adapters.HappyPlacesAdapter
import org.abubaker.happyplaces.database.DatabaseHandler
import org.abubaker.happyplaces.databinding.ActivityMainBinding
import org.abubaker.happyplaces.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    // Binding Object
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate Layout (XML)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        // FAB
        binding.fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)

            // It is only starting the activity, but
            // startActivity(intent)

            // But we need to also receive the RESULT from the completed Activity
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)

        }

        // Call the function to retrieve records
        getHappyPlacesListFromLocalDB()

    }

    // It will call the function from the DatabaseHandler.kt file
    private fun getHappyPlacesListFromLocalDB() {

        // Importing the DatabaseHandler.kt file from different folder:
        // i.e. import org.abubaker.happyplaces.database.DatabaseHandler
        val dbHandler = DatabaseHandler(this)

        // We are getting records from dbHandler.getHappyPlacesList()
        val getHappyPlacesList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        // Later on we will replace following code with RecyclerView
        if (getHappyPlacesList.size > 0) {

//            for (i in getHappyPlacesList) {
//                Log.e("Title", i.title)
//                Log.e("Description", i.description)
//            }

            binding.rvHappyPlacesList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)

        } else {
            binding.rvHappyPlacesList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }

    }

    /**
     * A function to populate the recyclerview to the UI.
     */
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {

        binding.rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        binding.rvHappyPlacesList.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlacesList)
        binding.rvHappyPlacesList.adapter = placesAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (requestCode == Activity.RESULT_OK) {
                getHappyPlacesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back pressed")
            }
        }

    }

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }

}