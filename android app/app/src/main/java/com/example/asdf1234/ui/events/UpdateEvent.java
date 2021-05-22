package com.example.asdf1234.ui.events;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.MainActivity;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateEvent extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText etUpdateEventName;
    String eventName, eventDate, eventId;
    Button button;
    ProgressBar progressBar;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    String stringDay, stringMonth, stringHour, stringMin;
    UrlService urlService = new UrlService();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_update_event);
        etUpdateEventName = findViewById(R.id.etUpdateEventName);

        eventId = getIntent().getStringExtra("event_id");
        eventName = getIntent().getStringExtra("event_name");
        etUpdateEventName.setText(eventName);

        progressBar = findViewById(R.id.pbarUpdateEvent);

        button = findViewById(R.id.btnUpdateEventDate);
        button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,year, month,day);
            datePickerDialog.show();
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month + 1;

        stringDay = Integer.toString(myday);
        stringMonth = Integer.toString(myMonth);

        if(myday < 10) {
            stringDay = "0" + stringDay;
        }

        if(myMonth < 10) {
            stringMonth = "0" + stringMonth;
        }

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        stringHour = Integer.toString(myHour);
        stringMin = Integer.toString(myMinute);

        if(myHour < 10) {
            stringHour = "0" + stringHour;
        }

        if(myMinute < 10) {
            stringMin = "0" + stringMin;
        }

        eventDate = stringMonth + "/" + stringDay + "/" + myYear + " " + stringHour+":"+stringMin;
        Log.d("Date", "createEvent: " + eventDate);
    }

    public void displayMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void submit(View view) {
        progressBar.setVisibility(View.VISIBLE);
        eventName = etUpdateEventName.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);
        eventDate = stringMonth + "/" + stringDay + "/" + myYear + " " + stringHour+":"+stringMin;
        Log.d("Date", "createEvent: " + eventDate);
        if(eventName.isEmpty()) {
            displayMessage("Fill in all of the fields");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "events/update_event.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if(response.equals("success")) {
                        Log.d("update_event", response);
                        showAlert(response, "Event has been updated");
                    } else {
                        Log.e("update_event", response);
                        showAlert("error", response);
                    }
                }, error -> {
            progressBar.setVisibility(View.INVISIBLE);
            Log.e("createEvent", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("eventName", eventName);
                params.put("eventDate", eventDate);
                params.put("eventId", eventId);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    if(title.equals("success")) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}