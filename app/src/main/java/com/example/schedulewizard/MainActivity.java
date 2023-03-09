package com.example.schedulewizard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Get the schedule url from database :
        String scheduleUrl = null;
        if (scheduleUrl == null) {
            //If there is no schedule url, go to the start activity
            Context context = getApplicationContext();
            startActivity(new Intent(context, StartActivity.class));
        } else {
            //If there is a schedule url, go to the schedule activity
            Context context = getApplicationContext();
            startActivity(new Intent(context, ScheduleActivity.class));
        }

    }
}