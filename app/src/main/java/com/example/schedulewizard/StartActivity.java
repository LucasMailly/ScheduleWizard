package com.example.schedulewizard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    //disable possibility to return to previous activity
    @Override
    public void onBackPressed() {
        //do nothing
    }
}