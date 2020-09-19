package com.example.bai03;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HTTP extends AppCompatActivity {
    private ImageView imageView;
    private TextView txtContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_t_t_p);

        this.imageView = (ImageView) this.findViewById(R.id.image);
        this.txtContent = (TextView) this.findViewById(R.id.txtContent);

        checkPermission();

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                load_image();
                //
            }
        });

        Button btnLoadText = findViewById(R.id.btnLoadText);
        btnLoadText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                load_text();
                //
            }
        });

    }

    void load_image()
    {
        if (checkInternetConnection())
        {
            String imageUrl = "http://www.fithou.edu.vn/images/logo/logo.png";

            // Create a task to download and display image.
            DownloadImageTask task = new DownloadImageTask(this.imageView);

            // Execute task (Pass imageUrl).
            task.execute(imageUrl);
        }
    }

    void load_text()
    {
        if (checkInternetConnection())
        {
            String webUrl = "http://25.io/toau/audio/sample.txt";

            // Create a task to download and display image.
            DownloadJsonTask task = new DownloadJsonTask(this.txtContent);

            // Execute task (Pass imageUrl).
            task.execute(webUrl);
        }
    }

    void checkPermission() {
        Log.d("PERM", "RUN");
        // Send SMS to 5556

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int p4 = checkSelfPermission(Manifest.permission.INTERNET);
            int p5 = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);

            List<String> permissions = new ArrayList<String>();


            if (p4 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.INTERNET);
            }
            if (p5 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 9999);
            } else {
                //sendSMS();
            }
        } else {
            //sendSMS();
        }
    }

    private boolean checkInternetConnection() {
        // Get Connectivity Manager
        ConnectivityManager connManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Details about the currently active default data network
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(this, "No default network is currently active", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(this, "Network is not connected", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(this, "Network OK", Toast.LENGTH_LONG).show();
        return true;
    }

    public class DownloadJsonTask
            // AsyncTask<Params, Progress, Result>
            extends AsyncTask<String, Void, String> {

        private TextView textView;

        public DownloadJsonTask(TextView textView)  {
            this.textView= textView;
        }

        @Override
        protected String doInBackground(String... params) {
            String textUrl = params[0];

            InputStream in = null;
            BufferedReader br= null;
            try {
                URL url = new URL(textUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                int resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    br= new BufferedReader(new InputStreamReader(in));

                    StringBuilder sb= new StringBuilder();
                    String s= null;
                    while((s= br.readLine())!= null) {
                        sb.append(s);
                        sb.append("\n");
                    }
                    return sb.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close
            }
            return null;
        }

        // When the task is completed, this method will be called
        // Download complete. Lets update UI
        @Override
        protected void onPostExecute(String result) {
            if(result  != null){
                this.textView.setText(result);
            } else{
                Log.e("MyMessage", "Failed to fetch data!");
            }
        }
    }


    public class DownloadImageTask
            // AsyncTask<Params, Progress, Result>
            extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        public DownloadImageTask(ImageView imageView)  {
            this.imageView= imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];

            InputStream in = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                int resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                } else {
                    return null;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                }catch (Exception e) {

                }
            }
            return null;
        }

        // When the task is completed, this method will be called
        // Download complete. Lets update UI
        @Override
        protected void onPostExecute(Bitmap result) {
            if(result  != null){
                this.imageView.setImageBitmap(result);
            } else{
                Log.e("MyMessage", "Failed to fetch data!");
            }
        }
    }
}