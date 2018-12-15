package com.parker.parker.activity.park;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parker.parker.R;
import com.parker.parker.activity.LoginActivity;
import com.parker.parker.app.AppController;
import com.parker.parker.app.ConnectionConfig;
import com.parker.parker.helper.SQLiteHandler;
import com.parker.parker.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ParkRegisterActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude, longitude;
    String String_latitude, String_longitude;
    LocationManager locationManager;
    private EditText inputCapacity;
    private EditText inputPrice;
    private EditText inputParkName;


    private SessionManager session;
    private SQLiteHandler db;
    private static final String TAG = ParkRegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    private String name;
    private String email;
    private String password;
    private String type;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_register);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        inputCapacity = (EditText) findViewById(R.id.capacity);
        inputParkName = (EditText) findViewById(R.id.parkName);
        inputPrice  = (EditText) findViewById(R.id.price);

        //get value from register activity
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        type = getIntent().getStringExtra("type");
        phone = getIntent().getStringExtra("phone");

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public void setLocation(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            buildAlertMessageNoGps();
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            getLocation();
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location0 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location0 != null) {
                latitude = location0.getLatitude();
                longitude = location0.getLongitude();
                LatLng current = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(current).title("Marker in current"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17));
            }
        }
    }

    public void btnRegister(View view) {
        String capacity = inputCapacity.getText().toString().trim();
        String parkName = inputParkName.getText().toString().trim();
        String price = inputPrice.getText().toString().trim();
        String_latitude =  Double.toString(latitude);
        String_longitude =  Double.toString(longitude);
        if (!name.isEmpty()) {
            registerUserPark(capacity, parkName, price);
        }
    }


    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUserPark(final String capacity, final String parkName, final String price) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, ConnectionConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String id = Integer.toString(jObj.getInt("id"));
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String type = user.getString("type");
                        String phone = user.getString("phone");
                        String created_at = user.getString("created_at");
                        String park_id = Integer.toString(jObj.getInt("park_id"));
                        JSONObject park = jObj.getJSONObject("park");
                        String latitude = park.getString("latitude");
                        String longitude = park.getString("longitude");
                        String capacity = park.getString("capacity");
                        String parkName = park.getString("parkName");
                        String price = park.getString("price");

                        //addPark(String user_id, String park_id, String latitude, String longitude, String capacity)
                        // Inserting row in users table
                        db.addUser(id, name, email, type, phone, created_at);
                        db.addPark(park_id, id, latitude, longitude, capacity, parkName, price);

                        // Launch login activity
                        Intent intent = new Intent(ParkRegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), "1. " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "2. " + error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("type", type);
                params.put("phone", phone);
                params.put("latitude", String_latitude);
                params.put("longitude", String_longitude);
                params.put("capacity", capacity);
                params.put("parkName", parkName);
                params.put("price", price);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
