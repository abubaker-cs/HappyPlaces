package org.abubaker.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import org.abubaker.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "HappyPlacesDatabase" // Database name
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    /**
     * Function to insert a Happy Place details to SQLite Database.
     */
    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {

        // We need to select current database for storing values
        val db = this.writableDatabase

        // Creates an empty set of values using the default initial size
        val contentValues = ContentValues()

        // TITLE
        contentValues.put(KEY_TITLE, happyPlace.title)

        // IMAGE
        contentValues.put(KEY_IMAGE, happyPlace.image)

        // DESCRIPTION
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)

        // DATE
        contentValues.put(KEY_DATE, happyPlace.date)

        // LOCATION
        contentValues.put(KEY_LOCATION, happyPlace.location)

        // LATITUDE
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)

        // LONGITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        // Inserting Row, 2nd argument is String containing nullColumnHack
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

        // Closing database connection
        db.close()

        return result
    }
    // END

    /**
     * Function to update record
     */
    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {

        // We need to select current database for storing values
        val db = this.writableDatabase

        // Creates an empty set of values using the default initial size
        val contentValues = ContentValues()

        // Title
        contentValues.put(KEY_TITLE, happyPlace.title)

        // Image
        contentValues.put(KEY_IMAGE, happyPlace.image)

        // Description
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)

        // Date
        contentValues.put(KEY_DATE, happyPlace.date)

        // Location
        contentValues.put(KEY_LOCATION, happyPlace.location)

        // LATITUDE
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)

        // LONGITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        // We want to update the database with contentValues
        // Note: 2nd argument is String containing nullColumnHack
        val success = db.update(TABLE_HAPPY_PLACE, contentValues, KEY_ID + "=" + happyPlace.id, null)

        // Closing database connection
        db.close()

        // Return results
        return success
    }

    /**
     * Function to delete happy place details.
     */
    fun deleteHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase
        // Deleting Row
        val success = db.delete(TABLE_HAPPY_PLACE, KEY_ID + "=" + happyPlace.id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    /**
     * Function to read all the list of Happy Places data which are inserted.
     */
    fun getHappyPlacesList(): ArrayList<HappyPlaceModel> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val happyPlaceList: ArrayList<HappyPlaceModel> = ArrayList()

        // Select Database using SQL Query
        val selectQuery = "SELECT  * FROM $TABLE_HAPPY_PLACE"

        // Read from the selected Database
        val db = this.readableDatabase

        // Error Handling (e): SQLiteException
        try {

            // It will cycle through all records in the selected TABLE
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            // Cursor = It contains the result set of a query made against a database
            // moveToFirst() = Moves the cursor to the first row.
            if (cursor.moveToFirst()) {

                // Fetch information of selected rows from each record
                do {

                    // Store records from the DB's Entry to the PLACE variable
                    val place = HappyPlaceModel(

                        // getInt = For Integer Record
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),

                        // getString = For Textual Records
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),

                        // getDouble = For Real Numbers
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )

                    // Add record to the List
                    happyPlaceList.add(place)

                } while (cursor.moveToNext())

            }

            // Close the Cursor so that our program does not run into errors
            cursor.close()

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        // Return the List of Records
        return happyPlaceList
    }
}