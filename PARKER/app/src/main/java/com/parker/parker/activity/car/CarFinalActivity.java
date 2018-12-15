package com.parker.parker.activity.car;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parker.parker.R;
import com.parker.parker.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class CarFinalActivity extends AppCompatActivity {

    private SQLiteHandler db;

    private double car_lon, car_lat, park_lat, park_lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_final);

        int position = getIntent().getIntExtra("position", 0);
        String parkAt_string = getIntent().getStringExtra("park_at");
        String parkIn_string = getIntent().getStringExtra("park_in");


        TextView parkAt = (TextView) findViewById(R.id.parkAt);
        TextView parkIn = (TextView) findViewById(R.id.parkIn);

        db = new SQLiteHandler(getApplicationContext());

        ArrayList<HashMap<String, String>> cars = db.getCarDetails();
        HashMap<String, String> car = cars.get(0);

        ArrayList<HashMap<String, String>> parks = db.getParkDetails();
        HashMap<String, String> park = parks.get(position);

        String park_name = park.get("name");

        car_lon = getIntent().getDoubleExtra("car_lon", 0);
        car_lat = getIntent().getDoubleExtra("car_lat", 0);
        park_lat = Double.valueOf(park.get("latitude"));
        park_lon = Double.valueOf(park.get("longitude"));

        parkAt.setText(parkAt_string);
        parkIn.setText(park_name);

    }

    public void openGoogleMaps(View view) {

        Intent googleMaps = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + car_lat + "," + car_lon +"&daddr=" + park_lat + "," + park_lon));
        startActivity(googleMaps);
    }
}
