package org.abubaker.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import org.abubaker.happyplaces.R
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
            startActivity(intent)
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
            for (i in getHappyPlacesList) {
                Log.e("Title", i.title)
                Log.e("Description", i.description)
            }
        }

    }

}