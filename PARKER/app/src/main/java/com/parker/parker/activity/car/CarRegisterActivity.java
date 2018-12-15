package com.parker.parker.activity.car;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.parker.parker.R;
import com.parker.parker.activity.LoginActivity;
import com.parker.parker.app.AppController;
import com.parker.parker.app.AsyncResponse;
import com.parker.parker.app.ConnectionConfig;
import com.parker.parker.app.NetworkAsyncTask;
import com.parker.parker.helper.SQLiteHandler;
import com.parker.parker.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CarRegisterActivity extends AppCompatActivity implements AsyncResponse {

    private static final String TAG = CarRegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    private TextView inputCarBody;
    private TextView inputCarColor;
    private TextView inputCarCompany;
    private TextView inputCarPlate;
    private EditText inputCarName;
    private SessionManager session;
    private SQLiteHandler db;

    private String name;
    private String email;
    private String password;
    private String type;
    private String phone;


    //image
    Bitmap carBitmap;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "parker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_register);

        inputCarPlate = (TextView) findViewById(R.id.carPlate);
        inputCarColor = (TextView) findViewById(R.id.carColor);
        inputCarCompany = (TextView) findViewById(R.id.carCompany);
        inputCarBody = (TextView) findViewById(R.id.carBody);
        inputCarName = (EditText) findViewById(R.id.carName);

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

    public void btnRegister(View view) {
        String plate = inputCarPlate.getText().toString().trim();
        String body = inputCarBody.getText().toString().trim();
        String company = inputCarCompany.getText().toString().trim();
        String color = inputCarColor.getText().toString().trim();
        String carName = inputCarName.getText().toString().trim();

        if (!plate.isEmpty() && !body.isEmpty() && !company.isEmpty() && !color.isEmpty()) {
            registerUserCar(plate, body, color, company, carName);
        }
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUserCar(final String plate, final String body, final String color, final String company, final String carName) {// edit by omar
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
                        String id = jObj.getString("id");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String type = user.getString("type");
                        String phone = user.getString("phone");
                        String created_at = user.getString("created_at");
                        String car_id = jObj.getString("car_id");
                        JSONObject car = jObj.getJSONObject("park");
                        String plate = car.getString("plate");
                        String body = car.getString("body");
                        String color = car.getString("color");
                        String company = car.getString("company");


                        // Inserting row in users table
                        db.addUser(id, name, email, type, phone, created_at);

                        // Launch login activity
                        Intent intent = new Intent(CarRegisterActivity.this, LoginActivity.class);
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
                params.put("plate", plate);
                params.put("body", body);
                params.put("color", color);
                params.put("company", company);
                params.put("carName", carName);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //btn to upload image
    public void btnTakePic(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                File photoFile = createImageFile();
                Toast.makeText(this ,photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Log.i("Mayank",photoFile.getAbsolutePath());
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    //camera use uri format not file format to we convert file to uri format
                    Uri photoURI = FileProvider.getUriForFile(this,"com.parker.parker.fileprovider",photoFile);
                    //first argument mean i want to save the pic and the second file location where it will be saved
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); //save pic
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
        }else {
            Toast.makeText(this,"Null", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile()throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Create an file External sdcard location
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Toast.makeText(this,"Unable to create directory.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        //save picture with file name create path with Dir and name
        File image = new File(storageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            //setPic();
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            myBitmap = rotateImage(myBitmap, 90);
            carBitmap = myBitmap;
            //mImageView.setImageBitmap(myBitmap);

            NetworkAsyncTask asyncTask = new NetworkAsyncTask(carBitmap, this);
            //this to set delegate/listener back to this class
            asyncTask.response = this;
            //execute the async task
            asyncTask.execute();

        }
        else {
            Toast.makeText(this,"Request cancelled or something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processFinish(String output) {
        String plate = null, color = null, make = null, BodyType = null;
        int colorConfidence = 0, makeConfidence = 0, BodyTypeConfidence = 0;
        try {
            JSONObject jsonObject = new JSONObject(output);
            JSONObject vehicle = null;

            int total = jsonObject.getJSONObject("processing_time").getInt("total");

            JSONArray arr = jsonObject.getJSONArray("results");

            for (int i = 0; i < arr.length(); i++) {
                plate = arr.getJSONObject(i).getString("plate");
                vehicle = arr.getJSONObject(i).getJSONObject("vehicle");
            }

            JSONArray arrVehicleColor = vehicle.getJSONArray("color");
            color = arrVehicleColor.getJSONObject(0).getString("name");
            colorConfidence = arrVehicleColor.getJSONObject(0).getInt("confidence");

            JSONArray arrVehicleMake = vehicle.getJSONArray("make");
            make = arrVehicleMake.getJSONObject(0).getString("name");
            makeConfidence = arrVehicleMake.getJSONObject(0).getInt("confidence");

            JSONArray arrVehicleBodyType = vehicle.getJSONArray("body_type");
            BodyType = arrVehicleBodyType.getJSONObject(0).getString("name");
            BodyTypeConfidence = arrVehicleBodyType.getJSONObject(0).getInt("confidence");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (colorConfidence < 50 || makeConfidence < 50 || BodyTypeConfidence < 50){
            Toast.makeText(getApplicationContext(), "please, take another pic", Toast.LENGTH_LONG).show();
            dispatchTakePictureIntent();
        }

        inputCarPlate.setText(plate);
        inputCarBody.setText(BodyType);
        inputCarCompany.setText(make);
        inputCarColor.setText(color);

    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
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
