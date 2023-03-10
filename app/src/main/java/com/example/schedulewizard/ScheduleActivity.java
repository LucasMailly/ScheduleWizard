package com.example.schedulewizard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    public void sync() {
        Toast.makeText(this, "Syncing... ", Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(() -> {
            Intent intent = getIntent();
            String icsFile;

            try {
                URL url = new URL(intent.getStringExtra("url"));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //Check that the response is a calendar file
                if (!urlConnection.getContentType().equals("text/calendar")) {
                    // Show error message and return to StartActivity
                    Log.d("ScheduleActivity", url.toString());
                    runOnUiThread(() -> Toast.makeText(this, "Calendrier invalide", Toast.LENGTH_SHORT).show());
                    return;
                }
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    icsFile = result.toString();
                    Log.d("ScheduleActivity", icsFile);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("ScheduleActivity", e.getMessage());
            }
        });

        thread.start();


    }

}