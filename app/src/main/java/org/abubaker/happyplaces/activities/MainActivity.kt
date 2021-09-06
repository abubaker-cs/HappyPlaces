package org.abubaker.happyplaces.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.adapters.HappyPlacesAdapter
import org.abubaker.happyplaces.database.DatabaseHandler
import org.abubaker.happyplaces.databinding.ActivityMainBinding
import org.abubaker.happyplaces.models.HappyPlaceModel
import org.abubaker.happyplaces.utils.SwipeToDeleteCallback
import org.abubaker.happyplaces.utils.SwipeToEditCallback

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

    // It is called when the activity which launched with the request code
    // and expecting a result from the launched activity.
    // Call Back method  to get the Message form other Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if the request code is same as what is passed  here it is 'ADD_PLACE_ACTIVITY_REQUEST_CODE'
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
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


        // STEP 01 - Bind the onclickListener with adapter onClick function
        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {

            // We are overriding our custom interface OnClickListener defined in the HappyPlacesAdapter.kt file
            override fun onClick(position: Int, model: HappyPlaceModel) {

                // Select the Activity which we want to run, i.e. HappyPlaceDetailActivity
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)

                // PutExtra will automatically rely on Parcelable
                intent.putExtra(EXTRA_PLACE_DETAILS, model)

                // Start the Activity
                startActivity(intent)

            }
        })

        /**
         * Bind the EDIT feature class to recyclerview
         */
        val editSwipeHandler = object : SwipeToEditCallback(this) {

            // onSwiped()
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // Call the adapter function when it is swiped
                val adapter = binding.rvHappyPlacesList.adapter as HappyPlacesAdapter

                // Send us to the add place activity screen, so we can edit the Activity
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )

            }
        }

        //
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvHappyPlacesList)

        /**
         * Bind the DELETE feature class to recyclerview
         */
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {

            // onSwiped()
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // Call the adapter function when it is swiped for delete
                val adapter = binding.rvHappyPlacesList.adapter as HappyPlacesAdapter

                // Remove current record
                adapter.removeAt(viewHolder.adapterPosition)

                // Refresh the LIST after delete the selected record
                getHappyPlacesListFromLocalDB()
            }
        }

        //
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvHappyPlacesList)

    }

    /**
     * Declare a static variable which we will using for notify the item is added
     * when we will be returning back after adding.
     */
    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "extra_place_details"
    }

}