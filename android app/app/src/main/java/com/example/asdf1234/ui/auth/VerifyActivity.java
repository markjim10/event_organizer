package com.example.asdf1234.ui.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyActivity extends AppCompatActivity {

    private final  String TAG = "VerifyActivity: ";
    ProgressBar progressBar;
    EditText etCode;
    String username, code;
    UrlService urlService = new UrlService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_verify);
        username = getIntent().getStringExtra("username");
    }

    public void verify(View view) {
        progressBar = findViewById(R.id.pbarVerify);
        progressBar.setVisibility(View.VISIBLE);

        etCode = findViewById(R.id.etCode);
        code = etCode.getText().toString();

        if(code.isEmpty()) {
            displayMessage("Fill in all of the fields");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "accounts/verify.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d(TAG, response);
                    if(response.equals("ok")) {
                        showAlert("Success", "Email validated");
                    } else {
                        showAlert("Error", response);
                    }
                }, error -> {
            progressBar.setVisibility(View.INVISIBLE);
            Log.e(TAG, error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("code", code);
                params.put("username", username);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    if(title.equals("Success")) {
                        Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    public void displayMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}