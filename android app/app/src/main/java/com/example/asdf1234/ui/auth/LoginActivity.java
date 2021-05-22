package com.example.asdf1234.ui.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.asdf1234.MainActivity;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText etUsername, etPassword;
    String username, password;
    UrlService urlService = new UrlService();
    SharedPreferences sharedPreferences;
    String TAG = "LoginActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        if (sharedPreferences.contains("user_id")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void login(View view) {

        progressBar = findViewById(R.id.pbarLogin);
        progressBar.setVisibility(View.VISIBLE);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if(username.isEmpty() || password.isEmpty()) {
            displayMessage("Fill in all of the fields");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "accounts/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d(TAG, response);
                    if(response.equals("Invalid credentials")) {
                        showAlert("Login Error", response);
                    } else if(response.equals("User not verified")) {
                        Intent intent = new Intent(this, VerifyActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        try {
                            JSONObject object = new JSONObject(response);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_id", object.getString("id"));
                            editor.putString("username", object.getString("username"));
                            editor.putString("email", object.getString("email"));
                            editor.putString("mobile", object.getString("mobile"));
                            editor.apply();

                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage() );
                        }
                    }
                }, error -> {
            progressBar.setVisibility(View.INVISIBLE);
            Log.e(TAG, error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
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

    public void displayMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    //
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}