package com.example.schedulewizard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;

public class ScheduleActivity extends AppCompatActivity {

    LinearLayout eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        eventListView = findViewById(R.id.eventListView);

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

    public Date UTC2Local(Date date) throws ParseException {
        TimeZone tz = TimeZone.getDefault();
        return new Date(date.getTime() + tz.getRawOffset());
    }

    public void sync() {
        Intent intent = getIntent();

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(intent.getStringExtra("url"));
                URLConnection urlConnection = url.openConnection();
                InputStream ical = new BufferedInputStream(urlConnection.getInputStream());
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(ical);
                intent.putExtra("calendar", calendar);
            } catch (IOException | ParserException e) {
                Log.e("ScheduleActivity", e.getMessage());
                startActivity(new Intent(ScheduleActivity.this, StartActivity.class));
            }
        });

        thread.start();

        //log the first event to test
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Calendar calendar = (Calendar) intent.getSerializableExtra("calendar");

        //order events by date
        ArrayList<CalendarComponent> events = new ArrayList<>(calendar.getComponents());
        events.sort((o1, o2) -> {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                String startDate1 = o1.getProperty("DTSTART").getValue();
                String startDate2 = o2.getProperty("DTSTART").getValue();
                Date start1 = format.parse(startDate1);
                Date start2 = format.parse(startDate2);
                return start1.compareTo(start2);
            } catch (Exception e) {
                Log.e("ScheduleActivity", e.getMessage());
                return 0;
            }
        });

        //clear the event list
        eventListView.removeAllViews();
        //add events to the event list if they are not in the past
        Date lastDate = null;
        SimpleDateFormat formatICS = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatDayName = new SimpleDateFormat("EEEE dd MMMM");
        for (CalendarComponent event : events) {
            try {
                Date today = new Date();

                // this two date are in UTC
                String startDate = event.getProperty("DTSTART").getValue();
                String endDate = event.getProperty("DTEND").getValue();
                // convert them to local time
                Date start = UTC2Local(formatICS.parse(startDate));
                Date end = UTC2Local(formatICS.parse(endDate));

                if (start.compareTo(today) < 0) {
                    continue;
                }

                String startTime = formatTime.format(start);
                String endTime = formatTime.format(end);

                String summary = event.getProperty("SUMMARY").getValue();
                String location = event.getProperty("LOCATION").getValue();
                String group = event.getProperty("DESCRIPTION").getValue().split("\n")[2];
                String teacher = event.getProperty("DESCRIPTION").getValue().split("\n")[3];

                if (lastDate == null || !formatDay.format(lastDate).equals(formatDay.format(start))) {

                    View dateView = getLayoutInflater().inflate(R.layout.class_day, eventListView, false);

                    TextView date = dateView.findViewById(R.id.day);

                    String todayDate = formatDay.format(today);
                    if (todayDate.equals(formatDay.format(start))) {
                        date.setText("Aujourd'hui");
                    } else if (todayDate.equals(formatDay.format(new Date(start.getTime() - 3600 * 24 * 1000)))) {
                        date.setText("Demain");
                    } else {
                        date.setText(formatDayName.format(start));
                    }
                    lastDate = start;

                    eventListView.addView(dateView);

                }

                View eventView = getLayoutInflater().inflate(R.layout.class_event, eventListView, false);

                TextView class_event = eventView.findViewById(R.id.event);

                class_event.setText(summary + "\n" + location + " - " + group + " " + teacher + "\n" + startTime + " - " + endTime);

                eventListView.addView(eventView);

            } catch (Exception e) {
                Log.e("ScheduleActivity", e.getMessage());
            }


        }

    }

}