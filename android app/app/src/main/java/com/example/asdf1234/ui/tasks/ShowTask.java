package com.example.asdf1234.ui.tasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.MainActivity;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowTask extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    String TAG = "ShowTask";
    String task_id, task_name, task_expenses, task_status, event_id, task_payment;
    UrlService urlService = new UrlService();
    RadioGroup radioGroup;
    RadioButton radioButton;
    ProgressBar progressBar;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> payments = new ArrayList<>();

    EditText etTaskName, etTaskExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);
        task_payment = "Full";

        payments.add("Full");
        payments.add("Installment");

        spinner = findViewById(R.id.spinneryPayment);
        spinner.setOnItemSelectedListener(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String stringTask = getIntent().getStringExtra("task");
        progressBar = findViewById(R.id.pBarShowTask);
        Log.d(TAG, "onCreate: " + stringTask);

        try {
            JSONObject jsonObject = new JSONObject(stringTask);
            event_id = jsonObject.getString("event_id");
            task_id = jsonObject.getString("task_id");
            task_name = jsonObject.getString("task_name");
            task_expenses = jsonObject.getString("task_expenses");
            task_status = jsonObject.getString("task_status");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        radioGroup = findViewById(R.id.radioGroup);

        etTaskName = findViewById(R.id.etShowTaskName);
        etTaskName.setText(task_name);

        etTaskExpenses = findViewById(R.id.etShowTaskExpenses);
        etTaskExpenses.setText(task_expenses);

        RadioButton rb;
        if(task_status.equals("pending")) {
            rb = findViewById(R.id.radioPending);
        } else {
            rb = findViewById(R.id.radioCompleted);
        }
        rb.setChecked(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        task_payment = arg0.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: " + task_payment);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    public void UpdateTask(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);

        task_name = etTaskName.getText().toString();
        task_expenses = etTaskExpenses.getText().toString();
        task_status = radioButton.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "tasks/update_task.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("update_task: ", response);
                    progressBar.setVisibility(View.INVISIBLE);
                    showAlert("success", "task has been updated");
                }, error -> {
            progressBar.setVisibility(View.INVISIBLE);
            Log.e("update_task: ", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", event_id);
                params.put("task_id", task_id);
                params.put("task_name", task_name);
                params.put("task_payment", task_payment);
                params.put("task_expenses", task_expenses);
                params.put("task_status", task_status);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowTask.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    Intent intent = new Intent(ShowTask.this, MainActivity.class);
                    startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

}