package org.abubaker.happyplaces

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.abubaker.happyplaces.databinding.ActivityAddHappyPlaceBinding
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

        // We are adding the onClick() listener for our image button
        binding.tvAddImage.setOnClickListener(this)

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

            R.id.tv_add_image -> {

                // From: Gallery
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")

                // From: Camera
                val pictureDialogItems =
                    arrayOf("Select photo from Gallery", "Capture photo from camera")

                // Action
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "Camera Selection coming soon...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // Initiate
                pictureDialog.show()


            }

        }
    }

    // From: Gallery
    private fun choosePhotoFromGallery() {
        // DEXTER will be used here
        // Reference:
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    Toast.makeText(
                        this@AddHappyPlaceActivity,
                        "Storage READ/WRITE permissions are granted. Now you can select an image from GALLERY",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check(); // onSameThread() - important to use it

    }

    //
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(
                "" +
                        "It looks like you have turned off permissions required for this feature." +
                        " It can be enabled under the Application Settings"
            ).setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
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