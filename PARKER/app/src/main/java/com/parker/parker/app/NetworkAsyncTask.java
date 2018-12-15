package com.parker.parker.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class NetworkAsyncTask extends AsyncTask<Void, Void, String> {

    // Progress dialog
    private final ProgressDialog pDialog;

    public AsyncResponse response = null;

    Bitmap carBitmap;
    Context context;

    public NetworkAsyncTask(Bitmap bitmap, Context context){
        this.context = context;
        this.carBitmap = bitmap;
        pDialog = new ProgressDialog(context);
    }

    protected void onPreExecute() {
        pDialog.setMessage("uploading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String json_content = "";
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            carBitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
            byte[] data = stream.toByteArray();

            // Encode file bytes to base64
            byte[] encoded = Base64.getEncoder().encode(data);

            // Setup the HTTPS connection to api.openalpr.com
            URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=eu&secret_key=" + "sk_be647b6269711857839165ab");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setFixedLengthStreamingMode(encoded.length);
            http.setDoOutput(true);

            // Send our Base64 content over the stream
            try(OutputStream os = http.getOutputStream()) {
                os.write(encoded);
            }

            int status_code = http.getResponseCode();
            if (status_code == 200)
            {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json_content += inputLine;
                in.close();


            }
            else
            {
                System.out.println("Got non-200 response: " + status_code);
            }


        }
        catch (MalformedURLException e)
        {
            System.out.println("Bad URL");
        }
        catch (IOException e)
        {
            System.out.println("Failed to open connection");
        }
        return json_content;
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(aVoid);
        pDialog.dismiss();
        response.processFinish(result);
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
