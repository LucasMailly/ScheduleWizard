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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Thread thread = new Thread(() -> {
            try {
                //connect to url and follow redirections
                Document doc = Jsoup.connect("https://cas.univ-tours.fr/cas/login?service=https://ent.univ-tours.fr/uPortal/Login%3FrefUrl%3D%2FuPortal%2Fp%2FMDW").followRedirects(true).get();
//              //
                doc.select("input[id=username]").attr("value", "lucas.mailly@etu.univ-tours.fr");
                doc.select("input[id=username]").attr("value", "lM9<BR*0mPtLv[]*=");
                doc.select("button[name=submit]").
                Log.d("StartActivity", "ok");
            } catch (MalformedURLException e) {
                Log.e("StartActivity", "MalformedURLException : " + e.getMessage());
            } catch (IOException e) {
                Log.e("StartActivity", "IOException : " + e.getMessage());
            } catch (Exception e) {
                Log.e("StartActivity", "Exception : " + e.getMessage());
            }
        });

        thread.start();
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
            Intent intent = new Intent(this, ScheduleActivity.class);
            String url = result.getContents();
            url = url.replace("http://", "https://");
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
            Intent intent = new Intent(StartActivity.this, ScheduleActivity.class);
            String url = input.getText().toString();
            url = url.replace("http://", "https://");
            intent.putExtra("url", url);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();

    }

}