package org.abubaker.happyplaces

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import org.abubaker.happyplaces.databinding.ActivityAddHappyPlaceBinding
import org.abubaker.happyplaces.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    // Binding Object
    private lateinit var binding: ActivityAddHappyPlaceBinding

    // Variables for Calendar
    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate Layout (XML)
        binding = DataBindingUtil.setContentView(
            this@AddHappyPlaceActivity,
            R.layout.activity_add_happy_place
        )

        // Enabling Support for the Toolbar
        setSupportActionBar(binding.toolbarAddPlace)

        // Activating the Toolbar
        val actionbar = supportActionBar

        //
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        // Navigate the main activity on clicking the back button inside the action bar.
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize DatePicker
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // final step
            updateDateInView()

        }

        // this = Because our AddHappyPlaceActivity is actually = View.OnClickListeners
        // IMPORTANT: It will BIND the DatePicker to this TextField = et_date,
        // i.e. the MODAL will popup from this location
        binding.etDate.setOnClickListener(this)

    }

    // After assigning View.OnClickListener to the main class
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    //
    private fun updateDateInView() {

        // Required Format
        val myFormat = "dd.MM.yyyy"

        // Simple Date Format = Get DEFAULT system format
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        // Convert current date based on the selected format and assign it to our et_date textfield
        binding.etDate.setText(sdf.format(cal.time).toString())
    }

}