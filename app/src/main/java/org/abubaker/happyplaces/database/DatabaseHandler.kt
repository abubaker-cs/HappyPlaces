package org.abubaker.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
        val db = this.writableDatabase

        // Based on HappyPlaceModelClass
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title) //  TITLE
        contentValues.put(KEY_IMAGE, happyPlace.image) // IMAGE
        contentValues.put(KEY_DESCRIPTION, happyPlace.description) // DESCRIPTION
        contentValues.put(KEY_DATE, happyPlace.date) // DATE
        contentValues.put(KEY_LOCATION, happyPlace.location) // LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.latitude) // LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude) // LONGITUDE

        // Inserting Row, 2nd argument is String containing nullColumnHack
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

        // Closing database connection
        db.close()

        return result
    }
    // END
}
// END