package com.example.schedulewizard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        setContentView(R.layout.activity_setting);
    }

    public void resetUrl(View view) {
        editor.putBoolean("firstTime", true);
        editor.putBoolean("returnAllowed", true);
        editor.apply();
        startActivity(new Intent(this, StartActivity.class));
    }
}