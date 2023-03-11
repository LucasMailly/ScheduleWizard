package com.example.schedulewizard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class StartActivity extends AppCompatActivity {

    private Boolean firstTime;
    private Boolean returnAllowed;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);
        firstTime = sharedPref.getBoolean("firstTime", true);
        url = sharedPref.getString("url", null);
        returnAllowed = sharedPref.getBoolean("returnAllowed", false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!firstTime && !returnAllowed) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {

        Log.d("ScheduleWizard", "onBackPressed: " + firstTime + " " + url);
        if (url != null && returnAllowed) {
            super.onBackPressed();
        }

    }

    public void scanQrCode(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            //pass url to ScheduleActivity without allowing user to return to StartActivity
            String url = result.getContents();
            url = url.replace("http://", "https://");
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(this, "URL invalide", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("url", url);

            SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstTime", false);
            editor.putString("url", url);
            editor.apply();

            startActivity(intent);
            finish();
        }
    });

    public void enterUrl(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Entrer l'URL :");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String url = input.getText().toString();
            url = url.replace("http://", "https://");
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(this, "URL invalide", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(StartActivity.this, ScheduleActivity.class);
            intent.putExtra("url", url);

            SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("url", url);
            editor.putBoolean("firstTime", false);
            editor.apply();

            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();

    }

}