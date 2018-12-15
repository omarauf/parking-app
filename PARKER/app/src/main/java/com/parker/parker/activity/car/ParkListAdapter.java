package com.parker.parker.activity.car;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parker.parker.R;
import com.parker.parker.activity.car.CarActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ParkListAdapter extends ArrayAdapter<HashMap<String, String>> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public ParkListAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String name = getItem(position).get("name");
        String capacity = getItem(position).get("capacity");
        String price = getItem(position).get("price");

        //Create the person object with the information
        //Person person = new Person(name,birthday,sex);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.listViewParkName);
        TextView tvCapacity = (TextView) convertView.findViewById(R.id.listViewCapacity);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.listViewPrice);

        double lat1 = Double.valueOf(getItem(position).get("latitude"));
        double lat2 = CarActivity.double_latitude;
        double lon1 = Double.valueOf(getItem(position).get("longitude"));
        double lon2 = CarActivity.double_longitude;
        double distance = distance(lat1, lat2, lon1, lon2);

        tvName.setText(name);
        tvCapacity.setText(Double.toString(distance));
        tvPrice.setText(price);


        return convertView;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}
