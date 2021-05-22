package com.example.asdf1234.ui.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.R;
import com.example.asdf1234.adapters.TaskListAdapter;
import com.example.asdf1234.models.Task;
import com.example.asdf1234.services.UrlService;
import com.example.asdf1234.ui.tasks.ShowTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShowEvent extends AppCompatActivity {

    String event_id, event_name, event_date, event_status, event_task_status;
    UrlService urlService = new UrlService();
    Map<Integer, Object> taskMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_show_event);
        String TAG = "ShowEvent";

        String stringEvent = getIntent().getStringExtra("event");
        Log.d(TAG, "onCreate: " + stringEvent);

        try {
            JSONObject jsonObject = new JSONObject(stringEvent);
            event_id = jsonObject.getString("event_id");
            event_name = jsonObject.getString("event_name");
            event_status = jsonObject.getString("event_status");
            event_date = jsonObject.getString("event_date");
            event_task_status = jsonObject.getString("event_task_status");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        if(event_status.equals("completed")) {
            Button btnUpdateEvent = findViewById(R.id.btnUpdateEvent);
            btnUpdateEvent.setVisibility(View.GONE);

            Button btnAddTask = findViewById(R.id.btnAddTaskToEvent);
            btnAddTask.setVisibility(View.GONE);

        }

        TextView tvShowEvent = findViewById(R.id.tvShowEvent);
        TextView tvShowDate = findViewById(R.id.tvShowDate);
        TextView tvShowTask = findViewById(R.id.tvShowTasks);

        tvShowEvent.setText(event_name);
        tvShowDate.setText(event_date);
        tvShowTask.setText(event_task_status);
        Log.d(TAG, "onCreate: " + event_task_status);

        getTasks();
    }

    public void updateEvent(View view) {
        Intent intent = new Intent(this, UpdateEvent.class);
        intent.putExtra("event_name", event_name);
        intent.putExtra("event_id", event_id);
        startActivity(intent);
    }

    private ArrayList getListData(String response) {
        ArrayList<Task> results = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                Task item = new Task();
                JSONObject obj = jsonArray.getJSONObject(i);
                taskMap.put(i, obj);
                item.setId(obj.getString("task_id"));
                item.setName(obj.getString("task_name"));
                item.setExpenses(obj.getString("task_expenses"));
                item.setStatus(obj.getString("task_status"));
                item.setPayment(obj.getString("task_payment"));
                item.setEventId(obj.getString("event_id"));
                results.add(item);
            }

        } catch (JSONException e) {
            Log.e("json err", e.getMessage());
        }
        return results;
    }

    public void getTasks() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "tasks/get_tasks.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("get_tasks: ", response);
                    ArrayList taskList = getListData(response);
                    final ProgressBar progressBar = findViewById(R.id.pbarShowEvent);
                    progressBar.setVisibility(View.GONE);
                    final ListView lv = findViewById(R.id.list_tasks);
                    lv.setVisibility(View.VISIBLE);
                    Parcelable state = lv.onSaveInstanceState();
                    lv.setAdapter(new TaskListAdapter(ShowEvent.this, taskList));
                    lv.onRestoreInstanceState(state);
                    lv.setOnItemClickListener((parent, view, position, id) -> {
                        Log.d("get_tasks: ", String.valueOf(taskMap.get(position)));
                        Intent intent = new Intent(getApplicationContext(), ShowTask.class);
                        intent.putExtra("task", String.valueOf(taskMap.get(position)));
                        startActivity(intent);
                    });

                }, error -> {
            Log.e("get_tasks", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", event_id);
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

    public void addTaskToEvent(View view) {
        Intent intent = new Intent(getApplicationContext(), AddTaskToEvent.class);
        intent.putExtra("event_id", event_id);
        intent.putExtra("event_name", event_name);
        startActivity(intent);
    }

    public void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}