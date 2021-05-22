package com.example.asdf1234.ui.events;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.MainActivity;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddTaskToEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayAdapter<String> adapterTasks;
    ArrayList<String> tasks = new ArrayList<>();
    ProgressBar progress;
    UrlService urlService = new UrlService();
    Map<Integer, String> taskMap = new HashMap<>();
    String task_id, event_id, event_name;
    Spinner spinTask;

    TextView textView;

    String TAG = "AddTaskToEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_to_event);
        progress = findViewById(R.id.pBarAddTask);

        event_id = getIntent().getStringExtra("event_id");
        event_name = getIntent().getStringExtra("event_name");

        textView = findViewById(R.id.labelAddTask);
        textView.setText("Add Task To " + event_name + " Event");

        spinTask = findViewById(R.id.spinnerTasks);
        spinTask.setOnItemSelectedListener(this);

        populate();

        adapterTasks = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tasks);
        adapterTasks.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTask.setAdapter(adapterTasks);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        task_id = taskMap.get(spinTask.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
        Log.d(TAG, "onNothingSelected: " + "nothing selected?");
    }

    public void populate() {

        SharedPreferences sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "tasks/available_tasks.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d("AddTaskToEvent", response);
                        progress.setVisibility(View.GONE);
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            taskMap.put(i, obj.getString("task_id"));
                            tasks.add(obj.getString("task_name"));
                        }
                        LinearLayout linearLayout = findViewById(R.id.linearIsTasksEmpty);
                        if(tasks.isEmpty()) {
                            linearLayout.setVisibility(View.GONE);
                            TextView textView = findViewById(R.id.isEmptyTask);
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            adapterTasks.notifyDataSetChanged();
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        Log.e("AddTaskToEvent", e.getMessage());
                    }
                }, error -> {
                    progress.setVisibility(View.GONE);
                    Log.i("AddTaskToEvent", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    public void addTask(View view) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "tasks/add_task.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progress.setVisibility(View.GONE);
                    Log.d(TAG, "add_task: " + response);
                    showAlert("Success", "Task has been added to the event");
                }, error -> {
                    progress.setVisibility(View.GONE);
                    Log.i("add_task: ", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", event_id);
                params.put("task_id", task_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTaskToEvent.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                Intent intent = new Intent(AddTaskToEvent.this, MainActivity.class);
                startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}