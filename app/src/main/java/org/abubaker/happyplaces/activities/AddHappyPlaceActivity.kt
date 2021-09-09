package org.abubaker.happyplaces.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.abubaker.happyplaces.BuildConfig
import org.abubaker.happyplaces.R
import org.abubaker.happyplaces.database.DatabaseHandler
import org.abubaker.happyplaces.databinding.ActivityAddHappyPlaceBinding
import org.abubaker.happyplaces.models.HappyPlaceModel
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

    // Important Variables for the project to run
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    // A variable for data model class in which we will receive the details to edit.
    // private var mHappyPlaceDetails: HappyPlaceModel? = null
    private var mHappyPlaceDetails: HappyPlaceModel? = null

    // A fused location client variable which will be used to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    /**
     * This function is auto created by Android when the Activity Class is created.
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

        // Initialize the Fused location variable
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /**
         * Initialize the places sdk if it is not initialized earlier using the api key.
         */
        // resources.getString(R.string.google_maps_api_key
        // Hide your API Key: https://www.youtube.com/watch?v=X8lYNW_Or2o&ab_channel=GoogleMapsPlatform
        if (!Places.isInitialized()) {
            Places.initialize(
                this@AddHappyPlaceActivity, BuildConfig.GMP_KEY
            )
        }

        // Assign the details to the variable of data model class which we have created
        // above the details which we will receive through intent.
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            // mHappyPlaceDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
            mHappyPlaceDetails =
                intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS) as? HappyPlaceModel
        }

        // Initialize DatePicker - create an OnDateSetListener
        // https://www.tutorialkart.com/kotlin-android/android-datepicker-kotlin-example/
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // final step
            updateDateInView()

        }

        // We are calling it twice - it will help in auto-updating the date
        updateDateInView()


        // Filling the existing details to the UI components to edit.
        if (mHappyPlaceDetails != null) {
            supportActionBar?.title = "Edit Happy Place"

            binding.etTitle.setText(mHappyPlaceDetails!!.title)
            binding.etDescription.setText(mHappyPlaceDetails!!.description)
            binding.etDate.setText(mHappyPlaceDetails!!.date)
            binding.etLocation.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)

            binding.ivPlaceImage.setImageURI(saveImageToInternalStorage)

            binding.btnSave.text = "UPDATE"
        }
        // END

        // this = Because our AddHappyPlaceActivity is actually = View.OnClickListeners
        // IMPORTANT: It will BIND the DatePicker to this TextField = et_date,
        // i.e. the MODAL will popup from this location
        binding.etDate.setOnClickListener(this)

        // We are adding the onClick() listener for our image button
        binding.tvAddImage.setOnClickListener(this)

        // For saving into the DB
        binding.btnSave.setOnClickListener(this)

        // Location (EditText)
        binding.etLocation.setOnClickListener(this)

        // Current Location
        binding.tvSelectCurrentLocation.setOnClickListener(this)

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

            // Save Button
            R.id.btn_save -> {
                // save the Data Model to the database
                // validate first

                when {

                    binding.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_LONG).show()
                    }

                    binding.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a description", Toast.LENGTH_LONG).show()
                    }

                    binding.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a date", Toast.LENGTH_LONG).show()
                    }

                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show()
                    }

                    else -> {

                        /**
                         * VERY IMPORTANT:
                         * ===============
                         * NEW RECORD = ID : 0
                         * EDITING RECORD = ID : current_id
                         */
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,
                            binding.etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding.etDescription.text.toString(),
                            binding.etDate.text.toString(),
                            binding.etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        // Refers to the DatabaseHandler.kt class
                        val dbHandler = DatabaseHandler(this)

                        // Call add or update details conditionally
                        if (mHappyPlaceDetails == null) {

                            // Add: New Entry

                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                            // If record was saved successfully
                            if (addHappyPlace > 0) {

                                // Send RESULT to the MainActivity (using companion variable)
                                setResult(Activity.RESULT_OK);

                                // Finishes the activity
                                finish()
                            }
                        } else {

                            // UPDATE: Existing Entry
                            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)

                            // If record was saved successfully
                            if (updateHappyPlace > 0) {

                                // Send RESULT to the MainActivity (using companion variable)
                                setResult(Activity.RESULT_OK);

                                // Finishes the activity
                                finish()//finishing activity

                            }
                        }

                    }

                }

            }

            R.id.et_location -> {

                try {

                    // List of our required fields
                    val fields = listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )

                    // Start the autocomplete intent with a unique request code.
                    val intent =
                        Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN,
                            fields
                        ).build(this@AddHappyPlaceActivity)

                    //
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Current Location
            R.id.tv_select_current_location -> {

                // If the Location is NOT enabled
                if (!isLocationEnabled()) {
                    Toast.makeText(
                        this,
                        "Your location provider is turned off. Please turn it on.",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redirect the user to Enable Locaiton Provider in the device
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                } else {

                    // If the location is Enabled

                    // For Getting current location of user please have a look at below link for better understanding
                    // https://www.androdocs.com/kotlin/getting-current-location-latitude-longitude-in-android-using-kotlin.html
                    Dexter.withContext(this)
                        .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        .withListener(object : MultiplePermissionsListener {

                            // 01 Display Notification is Accessing Current Location is Enabled
                            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                if (report!!.areAllPermissionsGranted()) {

                                    //
                                    requestNewLocationData()

                                    // Toast.makeText(
                                    //    this@AddHappyPlaceActivity,
                                    //    "Location permission is granted. Now you can request for a current location.",
                                    //    Toast.LENGTH_SHORT
                                    // ).show()

                                }
                            }

                            // 02 Ask user to provide permission for accessing Current Location
                            override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                            ) {
                                showRationalDialogForPermissions()
                            }

                        }).onSameThread()
                        .check()
                }
            }
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
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
                        saveImageToInternalStorage =
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

                saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)

                Log.e("Saved image: ", "Path :: $saveImageToInternalStorage")

                // Update imageView: ivPlaceImage
                binding.ivPlaceImage.setImageBitmap(thumbnail)

            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                // Extract "new place" from the received DATA
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)

                // Address
                binding.etLocation.setText(place.address)

                // Latitude
                mLatitude = place.latLng!!.latitude

                // Longitude
                mLongitude = place.latLng!!.longitude
            }

        }

    }

    /**
     * A function to update the selected date in the UI with selected format.
     * This function is created because every time we don't need to add format which we have added here to show it in the UI.
     */
    private fun updateDateInView() {

        // Required Format
        val myFormat = "dd.MM.yyyy"

        // Simple Date Format = Get DEFAULT system format
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        // Convert current date based on the selected format and assign it to our et_date text field
        binding.etDate.setText(sdf.format(cal.time).toString())
    }


    /**
     * A method is used for image selection from GALLERY / PHOTOS of phone storage.
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

    /**
     * A method is used  asking the permission for camera and storage and image capturing and selection from Camera.
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
        }).onSameThread().check() // onSameThread() - important to use it
    }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
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

    /**
     * A function to save a copy of an image to internal storage for HappyPlaceApp to use.
     */
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

    /**
     * A function which is used to verify that the location or let's GPS is enable or not of the user's device.
     */
    private fun isLocationEnabled(): Boolean {

        // We are asking for the Location Service
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Return if the Location Manager is Enabled or Disabled
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /**
     * A function to request the current location. Using the fused location provider client.
     * Reference: https://developer.android.com/training/location/retrieve-current
     */
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Variable to make a Location Request
        val mLocationRequest = LocationRequest()

        // Parameter: Location Accuracy
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Parameter: How many milliseconds do we want to run this?
        mLocationRequest.interval = 0

        // Parameter:
        mLocationRequest.fastestInterval = 0

        // Parameter: How many updates
        mLocationRequest.numUpdates = 1

        //
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // We are using our fused client to make a Location Request
        // Note: mLocationCallback is defined below in a separate function
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    /**
     * A location callback object of fused location provider client where we will get the current location details.
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            // Get the LAST location
            val mLastLocation: Location = locationResult!!.lastLocation

            // Latitude
            mLatitude = mLastLocation.latitude
            Log.e("Current Latitude", "$mLatitude")

            // Longitude
            mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")
        }
    }

    // Companion Objects
    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

}