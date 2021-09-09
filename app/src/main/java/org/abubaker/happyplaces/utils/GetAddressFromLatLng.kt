package org.abubaker.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.util.*

class GetAddressFromLatLng(
    context: Context, private val latitude: Double,
    private val longitude: Double
) : AsyncTask<Void, String, String>() {

    /**
     * Constructs a Geocoder whose responses will be localized for the
     * given Locale.
     *
     * @param context the Context of the calling Activity
     * @param locale the desired Locale for the query results
     *
     * @throws NullPointerException if Locale is null
     */
    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

    /**
     * A variable of address listener interface.
     */
    private lateinit var mAddressListener: AddressListener

    /**
     * Background method of AsyncTask where the background operation will be performed.
     */
    override fun doInBackground(vararg params: Void?): String {

        //
        try {
            /**
             * Returns an array of Addresses that are known to describe the
             * area immediately surrounding the given latitude and longitude.
             */

            // List of Results = List of Addresses using GeoCoder
            // Two options: getFromLocation() or getFromLocationName()
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            // If the List is not empty, then:
            if (addressList != null && addressList.isNotEmpty()) {

                // Get address list from the index-0
                /**
                 * A class representing an Address, i.e, a set of Strings describing a location.
                 *
                 * The address format is a simplified version of xAL (eXtensible Address Language)
                 * http://www.oasis-open.org/committees/ciq/ciq.html#6
                 */
                val address: Address = addressList[0]

                // We are creating a StringBuilder object
                val sb = StringBuilder()

                // In order to go through all the addresses which we have,
                for (i in 0..address.maxAddressLineIndex) {

                    // Since the ADDRESS object will return a lot of details, such as i.e:
                    // Name, Address, Admin Area, Locality, Postal Code, Country, Latitude,
                    // Longitude, Phone, URL. So we need to append , at the end of the parameters
                    // so they can be easily separated
                    sb.append(address.getAddressLine(i)).append(",")

                }

                // Here we remove the last comma that we have added above from the address.
                sb.deleteCharAt(sb.length - 1)

                //
                return sb.toString()

            }

        } catch (e: IOException) {

            // Printout the error
            Log.e("HappyPlaces", "Unable connect to Geocoder")

        }

        return ""
    }

    /**
     * onPostExecute method of AsyncTask where the result will be received and assigned to the interface accordingly.
     */
    override fun onPostExecute(resultString: String?) {

        //
        if (resultString == null) {

            //
            mAddressListener.onError()

        } else {

            //
            mAddressListener.onAddressFound(resultString)

        }

        //
        super.onPostExecute(resultString)
    }

    /**
     * A public function to set the AddressListener.
     */
    fun setAddressListener(addressListener: AddressListener) {

        //
        mAddressListener = addressListener

    }

    /**
     * A public function to execute the AsyncTask from the class is it called.
     */
    fun getAddress() {

        //
        execute()

    }

    /**
     * A interface for AddressListener which contains the function like success and error.
     */
    interface AddressListener {
        fun onAddressFound(address: String?)
        fun onError()
    }
}