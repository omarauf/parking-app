package com.parker.parker.activity.car;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.parker.parker.R;
import com.parker.parker.app.AppController;
import com.parker.parker.app.ConnectionConfig;
import com.parker.parker.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CarActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public static double double_latitude;
    public static double double_longitude;
    String String_latitude, String_longitude;
    LocationManager locationManager;
    private SQLiteHandler db;
    ListView nearestParksListView;
    ListAdapter adapter;
    private String car_id;
    String[] parker_id, user_id, latitude, longitude, capacity, parkNames, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        db = new SQLiteHandler(getApplicationContext());
        nearestParksListView = (ListView) findViewById(R.id.nearestParksListView);

        ArrayList<HashMap<String, String>> cars = db.getCarDetails();
        HashMap<String, String> car = cars.get(0);
        car_id = car.get("car_id");

        nearestParksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CarActivity.this);
                builder.setTitle("Are you sure ?")
                        .setMessage("Do you want to park here")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPark(position);
                            }
                        })
                        .setNegativeButton("No",null) ;
                AlertDialog alert = builder.create();
                alert.setTitle("Alert !!!");
                alert.show();


            }
        });

    }

    //URL_REQUEST_PARK
    private void requestPark(final int position) {
        String tag_string_req = "request_park";
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, ConnectionConfig.URL_REQUEST_PARK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        String park_in = Integer.toString(jObj.getInt("park_in"));
                        String park_at = jObj.getString("park_at");

                        Intent intent;
                        intent = new Intent(CarActivity.this, CarFinalActivity.class);
                        intent.putExtra("park_in", park_in);
                        intent.putExtra("park_at", park_at);
                        intent.putExtra("park_name", parkNames[position]);
                        intent.putExtra("position", position);

                        intent.putExtra("car_lat", double_latitude);
                        intent.putExtra("car_lon", double_longitude);

                        startActivity(intent);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url

                Map<String, String> params = new HashMap<String, String>();
                params.put("park_in", parker_id[position]);
                params.put("car_id", car_id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location0 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location0 != null) {
                double_latitude = location0.getLatitude();
                double_longitude = location0.getLongitude();
            }
        }
    }

    public void FindCar(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            buildAlertMessageNoGps();
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            getLocation();

        String_latitude =  Double.toString(double_latitude);
        String_longitude =  Double.toString(double_longitude);

        findCarRequest();
    }


    /**
     * function to verify login details in mysql db
     * */
    private void findCarRequest() {
        String tag_string_req = "find_car";
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, ConnectionConfig.URL_FIND_PARK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        JSONArray nearestParks = jObj.getJSONArray("nearestParks");
                        parker_id = new String[nearestParks.length()];
                        user_id = new String[nearestParks.length()];
                        latitude = new String[nearestParks.length()];
                        longitude = new String[nearestParks.length()];
                        capacity = new String[nearestParks.length()];
                        parkNames = new String[nearestParks.length()];
                        price = new String[nearestParks.length()];
                        for (int i = 0; i < nearestParks.length(); i++) {
                            parker_id[i] = Integer.toString(nearestParks.getJSONObject(i).getInt("parker_id"));
                            user_id[i] = Integer.toString(nearestParks.getJSONObject(i).getInt("user_id"));
                            latitude[i] = Double.toString(nearestParks.getJSONObject(i).getDouble("latitude"));
                            longitude[i] = Double.toString(nearestParks.getJSONObject(i).getDouble("longitude"));
                            capacity[i] = Integer.toString(nearestParks.getJSONObject(i).getInt("capacity"));
                            parkNames[i] = nearestParks.getJSONObject(i).getString("name");
                            price[i] = Integer.toString(nearestParks.getJSONObject(i).getInt("price"));
                        }
                        db.deleteParks();
                        for (int i = 0; i < nearestParks.length(); i++){
                            db.addPark(user_id[i], parker_id[i], latitude[i], longitude[i], capacity[i], parkNames[i], price[i]);
                        }

                        ArrayList<HashMap<String, String>> parks = db.getParkDetails();
                        ParkListAdapter adapter = new ParkListAdapter(getApplicationContext(), R.layout.adapter_view_layout, parks);
                        nearestParksListView.setAdapter(adapter);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", String_latitude);
                params.put("longitude", String_longitude);

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
