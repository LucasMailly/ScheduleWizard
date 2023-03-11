package com.example.schedulewizard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();

    }

}