package org.abubaker.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    // Binding Object
    private lateinit var binding: ActivityAddHappyPlaceBinding

    // Variables for Calendar
    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener


    /**
     * onCreate()
     */
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

    /**
     * onClick() - After assigning View.OnClickListener to the main class
     */
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

            // We are creating a custom Dialog, which will contain 2 actions
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
                        1 -> takePhotoFromCamera()
                    }
                }

                // Initiate
                pictureDialog.show()

            }

        }
    }

    /**
     * onActivityResult()
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Depreciated - onActivityResult()
        super.onActivityResult(requestCode, resultCode, data)

        // Take appropriate action, based on selected option from the custom dialog
        if (resultCode == Activity.RESULT_OK) {

            // Gallery - Allow user to select existing picture from the gallery
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {

                        // Pick the image
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        //
                        val saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)

                        Log.e("Saved image: ", "Path :: $saveImageToInternalStorage")

                        // Set the selected image
                        binding.ivPlaceImage.setImageBitmap(selectedImageBitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "Failed to load the image from Gallery",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // CAMERA - Allows user to take live picture using device camera
            } else if (requestCode == CAMERA) {

                // Convert retrieved data as Bitmap
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                val saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)

                Log.e("Saved image: ", "Path :: $saveImageToInternalStorage")

                // Update imageView: ivPlaceImage
                binding.ivPlaceImage.setImageBitmap(thumbnail)

            }

        }

    }

    /**
     * From: Camera
     */
    private fun takePhotoFromCamera() {
        // DEXTER will be used here
        // Reference:
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {

                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    // Open Camera
                    startActivityForResult(galleryIntent, CAMERA)

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

    /**
     * From: Gallery
     */
    private fun choosePhotoFromGallery() {
        // DEXTER will be used here
        // Reference:
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {

                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )

                    // Open Gallery
                    startActivityForResult(galleryIntent, GALLERY)

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

    // It will store image and then return URI (path of the file)
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)

        // Context.MODE_PRIVATE= The file will be only accessible by this application
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        //
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Compress
            val stream: OutputStream = FileOutputStream(file)

            // Compress image
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush & Close the active stream
            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        // It will return the ABSOLUTE path (whole directory)
        return Uri.parse(file.absolutePath)


    }

    // Companion Objects
    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }

}