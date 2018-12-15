package com.parker.parker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "admin_parker";

    //  table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_PARK = "parks";
    private static final String TABLE_CAR = "cars";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid"; //User_ID
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_CREATED_AT = "created_at";

    // Parker Table Columns names
    private static final String KEY_PARK_ID = "pid";
    private static final String KEY_PARK_PID = "park_id";
    private static final String KEY_PARK_UID = "user_id";
    private static final String KEY_PARK_LATITUDE = "latitude";
    private static final String KEY_PARK_LONGITUDE = "longitude";
    private static final String KEY_PARK_CAPACITY = "capacity";
    private static final String KEY_PARK_NAME = "name";
    private static final String KEY_PARK_PRICE = "price";

    // Parker Table Columns names
    private static final String KEY_CAR_ID = "pid";
    private static final String KEY_CAR_CID = "car_id";
    private static final String KEY_CAR_UID = "user_id";
    private static final String KEY_CAR_PLATE = "plate";
    private static final String KEY_CAR_BODY = "body";
    private static final String KEY_CAR_COLOR = "color";
    private static final String KEY_CAR_COMPANY = "company";
    private static final String KEY_CAR_NAME = "name";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
        // Create tables again
        onCreate(db);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_TYPE + " TEXT,"
                + KEY_PHONE + " TEXT," + KEY_CREATED_AT + " TEXT"  + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_PARK_TABLE = "CREATE TABLE " + TABLE_PARK + "("
                + KEY_PARK_ID + " INTEGER PRIMARY KEY," + KEY_PARK_PID + " TEXT," + KEY_PARK_UID + " TEXT,"
                + KEY_PARK_LATITUDE + " TEXT," + KEY_PARK_LONGITUDE + " TEXT,"
                + KEY_PARK_CAPACITY + " TEXT," + KEY_PARK_NAME + " TEXT," + KEY_PARK_PRICE + " TEXT" + ")";
        db.execSQL(CREATE_PARK_TABLE);

        String CREATE_CAR_TABLE = "CREATE TABLE " + TABLE_CAR + "("
                + KEY_CAR_ID + " INTEGER PRIMARY KEY," + KEY_CAR_CID + " TEXT," + KEY_CAR_UID + " TEXT,"
                + KEY_CAR_PLATE + " TEXT," + KEY_CAR_BODY + " TEXT,"
                + KEY_CAR_COLOR + " TEXT," + KEY_CAR_COMPANY + " TEXT," + KEY_CAR_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CAR_TABLE);

        Log.d(TAG, "Database tables created");
    }

    //------------------------------USER-------------------------------------------

    /**
     * Storing user details in database
     * */
    public void addUser(String user_id, String name, String email, String type, String phone, String created_at) { //add type by omar
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, user_id); // Name
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_TYPE, type); // add by omar type
        values.put(KEY_PHONE, phone); // add by omar phone
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("user_id", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("type", cursor.getString(4)); // add by omar
            user.put("phone", cursor.getString(5)); // add by omar
            user.put("created_at", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    //------------------------------PARK-------------------------------------------//


    /**
     * Storing Park details in database
     * */
    public void addPark(String user_id, String park_id, String latitude, String longitude, String capacity, String name, String price) { //add type by omar
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PARK_PID, park_id);
        values.put(KEY_PARK_UID, user_id);
        values.put(KEY_PARK_LATITUDE, latitude);
        values.put(KEY_PARK_LONGITUDE, longitude);
        values.put(KEY_PARK_CAPACITY, capacity);
        values.put(KEY_PARK_NAME, name);
        values.put(KEY_PARK_PRICE, price);
        // Inserting Row
        db.insert(TABLE_PARK, null, values);
        db.close(); // Closing database connection

        //Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting Park data from database
     **/
    public ArrayList<HashMap<String, String>> getParkDetails() {
        ArrayList<HashMap<String, String>> parks = new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_PARK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> park = new HashMap<String, String>();
                park.put("park_id", cursor.getString(1));
                park.put("user_id", cursor.getString(2));
                park.put("latitude", cursor.getString(3));
                park.put("longitude", cursor.getString(4));
                park.put("capacity", cursor.getString(5));
                park.put("name", cursor.getString(6));
                park.put("price", cursor.getString(7));

                parks.add(park);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching user from Sqlite: " + parks.toString());

        return parks;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteParks() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PARK, null, null);
        db.close();

        Log.d(TAG, "Deleted all park info from sqlite");
    }

    //------------------------------CARS-------------------------------------------//


    /**
     * Storing Car details in database
     * */
    public void addCar(String user_id, String car_id, String plate, String body, String color, String company, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CAR_CID, car_id);
        values.put(KEY_CAR_UID, user_id);
        values.put(KEY_CAR_PLATE, plate);
        values.put(KEY_CAR_BODY, body);
        values.put(KEY_CAR_COLOR, color);
        values.put(KEY_CAR_COMPANY, company);
        values.put(KEY_CAR_NAME, name);
        // Inserting Row
        db.insert(TABLE_CAR, null, values);
        db.close(); // Closing database connection

        //Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting Car data from database
     **/
    public ArrayList<HashMap<String, String>> getCarDetails() {
        ArrayList<HashMap<String, String>> cars = new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_CAR;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> car = new HashMap<String, String>();
                car.put("car_id", cursor.getString(1));
                car.put("user_id", cursor.getString(2));
                car.put("plate", cursor.getString(3));
                car.put("body", cursor.getString(4));
                car.put("color", cursor.getString(5));
                car.put("company", cursor.getString(6));
                car.put("name", cursor.getString(7));

                cars.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching user from Sqlite: " + parks.toString());

        return cars;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteCars() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CAR, null, null);
        db.close();

        Log.d(TAG, "Deleted all park info from sqlite");
    }


}
