package com.parker.parker.activity.park;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.parker.parker.R;
import com.parker.parker.activity.LoginActivity;
import com.parker.parker.activity.car.CarActivity;
import com.parker.parker.activity.car.ParkListAdapter;
import com.parker.parker.app.AppController;
import com.parker.parker.app.ConnectionConfig;
import com.parker.parker.helper.SQLiteHandler;
import com.parker.parker.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParkActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private ProgressDialog pDialog;

    ListView listViewCar;
    ArrayAdapter<String> adapter;
    String[] plate;
    String[] park_in, car_id;
    private String park_id, price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);

        listViewCar = (ListView) findViewById(R.id.listViewCar);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ArrayList<HashMap<String, String>> parks = db.getParkDetails();
        HashMap<String, String> park = parks.get(0);
        String capacity = park.get("capacity");
        String latitude = park.get("latitude");
        String longitude = park.get("longitude");
        park_id = park.get("park_id");
        String user_id = park.get("user_id");
        price = park.get("price");

        listViewCar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ParkActivity.this);
                builder.setTitle("Are you sure ?")
                        .setMessage("Do you want to check out the car")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkOut(position);
                            }
                        })
                        .setNegativeButton("No",null);
                AlertDialog alert = builder.create();
                alert.setTitle("Alert !!!");
                alert.show();

            }
        });

    }

    private void checkOut(final int position) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, ConnectionConfig.URL_CHECKOUT_CAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        String total_price = Integer.toString(jObj.getInt("price"));
                        String fate = jObj.getString("price");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ParkActivity.this);
                        builder.setTitle("The Price for this car is ")
                                .setMessage(total_price)
                                .setPositiveButton("Yes",null)
                                .setNegativeButton("No",null);
                        AlertDialog alert = builder.create();
                        alert.setTitle("Alert !!!");
                        alert.show();
                        getCars();
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
                params.put("park_id", park_in[position]);
                params.put("car_id", car_id[position]);
                params.put("price", price);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void allCarInPark(View view) {
        getCars();
    }

    private void getCars() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, ConnectionConfig.URL_ALL_CAR_IN_PARK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        JSONArray cars = jObj.getJSONArray("cars");
                        car_id = new String[cars.length()];
                        String[] user_id = new String[cars.length()];
                        plate = new String[cars.length()];
                        String[] body = new String[cars.length()];
                        String[] color = new String[cars.length()];
                        String[] company = new String[cars.length()];
                        String[] carNames = new String[cars.length()];
                        String[] park_at = new String[cars.length()];
                        park_in = new String[cars.length()];
                        for (int i = 0; i < cars.length(); i++) {
                            car_id[i] = Integer.toString(cars.getJSONObject(i).getInt("car_id"));
                            user_id[i] = Integer.toString(cars.getJSONObject(i).getInt("user_id"));
                            plate[i] = cars.getJSONObject(i).getString("plate");
                            body[i] = cars.getJSONObject(i).getString("body");
                            color[i] = cars.getJSONObject(i).getString("color");
                            company[i] = cars.getJSONObject(i).getString("company");
                            carNames[i] = cars.getJSONObject(i).getString("name");
                            park_at[i] = cars.getJSONObject(i).getString("park_at");
                            park_in[i] = Integer.toString(cars.getJSONObject(i).getInt("park_in"));
                        }
                        db.deleteCars();
                        for (int i = 0; i < cars.length(); i++){
                            db.addCar(user_id[i], car_id[i], plate[i], body[i], color[i], company[i], carNames[i]);
                        }
                        adapter = new ArrayAdapter<String>(getApplicationContext() ,android.R.layout.simple_list_item_1, plate);
                        listViewCar.setAdapter(adapter);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        listViewCar.setAdapter(adapter);
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
                params.put("park_id", park_id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
