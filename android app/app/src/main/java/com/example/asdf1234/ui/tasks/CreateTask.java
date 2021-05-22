package com.example.asdf1234.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.MainActivity;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import java.util.HashMap;
import java.util.Map;

public class CreateTask extends AppCompatActivity {

    String TAG = "create_task.php";
    EditText etTaskName, etTaskExpense;
    String task_name, task_expense;
    UrlService urlService = new UrlService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        etTaskName = findViewById(R.id.etCreateTaskName);
        etTaskExpense = findViewById(R.id.etCreateTaskExpense);


    }

    public void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    public void submit(View view) {
        task_name = etTaskName.getText().toString();
        task_expense = etTaskExpense.getText().toString();
        if (task_name.isEmpty() || task_expense.isEmpty()) {
            displayMessage("Fill in all of the fields");
        } else {
            create();
        }
    }

    public void create() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);

        ProgressBar progressBar = findViewById(R.id.pbarCreateTask);
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "tasks/create_task.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, response);
                    if(response.equals("success")) {
                        showAlert("Success", "Task has been created");
                        etTaskName.setText("");
                        etTaskExpense.setText("");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }, error -> {
                        Log.e(TAG, error.toString());
                        progressBar.setVisibility(View.INVISIBLE);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_name", task_name);
                params.put("task_expense", task_expense);
                params.put("user_id", user_id);
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

}